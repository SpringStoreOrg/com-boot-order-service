package com.boot.order.service;

import com.boot.order.client.CartServiceClient;
import com.boot.order.client.ProductServiceClient;
import com.boot.order.dto.*;
import com.boot.order.enums.OrderState;
import com.boot.order.exception.EntityNotFoundException;
import com.boot.order.model.Order;
import com.boot.order.model.OrderEntry;
import com.boot.order.model.OrderHistory;
import com.boot.order.repository.OrderHistoryRepository;
import com.boot.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.mail.MessagingException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.AFTER_ROLLBACK;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderHistoryRepository orderHistoryRepository;

    private final CartServiceClient cartServiceClient;

    private final ProductServiceClient productServiceClient;

    private final EmailService emailService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ModelMapper modelMapper;

    @Value("${estimated.days.to.deliver}")
    private int estimatedDaysToDeliver;

    @Transactional
    public OrderGetDTO createNewOrder(OrderDTO orderDto, String email, Long userId) {
        log.info("Subtract products {}", orderDto.getEntries().stream()
                .map(item -> String.format("name:%s quantity:%s",  item.getSlug(), item.getQuantity()))
                .collect(Collectors.joining(";")));
        List<StockDTO> reserveCall = orderDto.getEntries().stream()
                .map(item->modelMapper.map(item, StockDTO.class))
                .collect(Collectors.toList());
        List<StockDTO> reserveCallResult = productServiceClient.reserveProducts(reserveCall).getBody();
        applicationEventPublisher.publishEvent(new OrderPersistFailedEvent(reserveCallResult));

        Order order = modelMapper.map(orderDto, Order.class);
        order.setUuid(UUID.randomUUID());
        order.setUserId(userId);
        order.setState(OrderState.CREATED);
        if (order.getReceiptAddress() == null) {
            order.setReceiptAddress(order.getShippingAddress());
        }

        List<OrderEntry> newOrderEntries = new ArrayList<>();

        double orderTotal = 0;
        int productCount = 0;
        for (OrderEntryDTO entry : orderDto.getEntries()) {
            OrderEntry orderEntry = modelMapper.map(entry, OrderEntry.class);
            orderEntry.setOrder(order);
            Optional<StockDTO> optionalStockDTO = reserveCallResult.stream().filter(item -> item.getProductSlug().equals(entry.getSlug())).findFirst();
            if (optionalStockDTO.isPresent()) {
                orderEntry.setOutOfStock(optionalStockDTO.get().getNotInStock());
            } else {
                orderEntry.setOutOfStock(0);
            }
            orderTotal += entry.getPrice() * entry.getQuantity();
            productCount += entry.getQuantity();
            newOrderEntries.add(orderEntry);
        }

        order.setEntries(newOrderEntries);
        order.setTotal(orderTotal);
        order.setProductCount(productCount);
        order.setDeliveryDate(addBusinessDays(LocalDate.now(), estimatedDaysToDeliver));

        Order persistedOrder = orderRepository.save(order);

        persistOrderHistory(order);

        log.info("Order for User: {} saved!", email);

        applicationEventPublisher.publishEvent(new OrderCreatedEvent(order));

        return modelMapper.map(persistedOrder, OrderGetDTO.class);
    }

    @Transactional
    public void cancelOrder(String orderId) {
        log.info("Deleting order {}", orderId);
        Optional<Order> orderOptional = orderRepository.getFirstByUuid(UUID.fromString(orderId));
        if (orderOptional.isEmpty()) {
            throw new EntityNotFoundException("Order could not be found");
        }

        Order order = orderOptional.get();
        List<OrderEntryDTO> entries = order.getEntries().stream()
                .map(item -> modelMapper.map(item, OrderEntryDTO.class))
                .collect(Collectors.toList());
        List<StockDTO> releaseRequest = order.getEntries().stream()
                .filter(entry -> entry.getQuantity() > entry.getOutOfStock())
                .map(entry -> new StockDTO()
                        .setProductSlug(entry.getProductSlug())
                        .setQuantity(entry.getQuantity() - entry.getOutOfStock()))
                .collect(Collectors.toList());
        productServiceClient.releaseProducts(releaseRequest);
        log.info("Releasing products {}", entries.stream()
                .map(item -> String.format("name:%s quantity:%s", item.getSlug(),  item.getQuantity()))
                .collect(Collectors.joining(";")));

        order.setState(OrderState.CANCELLED);
        orderRepository.save(order);

        persistOrderHistory(order);
        log.info("Cancelled order {} for user {}", orderId, order.getReceiptAddress().getEmail());
    }

    public List<OrderGetDTO> getOrders(Long userId){
        List<Order> orders = orderRepository.getAllByUserIdOrderByCreatedOnDesc(userId);
        return orders.stream()
                .map(item->modelMapper.map(item, OrderGetDTO.class))
                .collect(Collectors.toList());
    }

    public OrderGetDetailsDTO getOrder(String orderId){
        Optional<Order> orderOptional = orderRepository.getFirstByUuid(UUID.fromString(orderId));
        if (orderOptional.isEmpty()) {
            throw new EntityNotFoundException("Order could not be found");
        }

        OrderGetDetailsDTO result =  modelMapper.map(orderOptional.get(), OrderGetDetailsDTO.class);
        Map<String, ProductDTO> productsMap = getProductDTOS(orderOptional.get().getEntries());
        result.getEntries().forEach(item->{
            ProductDTO product = productsMap.get(item.getProductSlug());
            if(product!=null && !product.getImages().isEmpty()){
                    item.setProductImg(product.getImages().get(0));
            }
        });

        return result;
    }

    @TransactionalEventListener(phase = AFTER_ROLLBACK)
    public void rollbackProductsReserve(OrderPersistFailedEvent productReservedEvent) {
        if (productReservedEvent != null) {
            log.info("Add product service as compensating action for {}", productReservedEvent.getReservedProducts().stream()
                    .map(item -> String.format("name:%s quantity:%s", item.getProductSlug(),  item.getQuantity()-item.getNotInStock()))
                    .collect(Collectors.joining(";")));
            List<StockDTO> releaseCall = productReservedEvent.getReservedProducts().stream()
                    .map(item->new StockDTO()
                            .setProductSlug(item.getProductSlug())
                            .setQuantity(item.getQuantity()-item.getNotInStock())
                    )
                    .collect(Collectors.toList());
            productServiceClient.releaseProducts(releaseCall);
            log.info("Add product service as compensating action finished for {}", productReservedEvent.getReservedProducts().stream()
                    .map(item -> String.format("name:%s quantity:%s", item.getProductSlug(),  item.getQuantity()-item.getNotInStock()))
                    .collect(Collectors.joining(";")));
        }
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void commitOrderCreation(OrderCreatedEvent orderCreatedEvent) {
        if (orderCreatedEvent != null) {
            if (orderCreatedEvent.getOrder().getUserId() != null) {
                cartServiceClient.deleteCartByUserId(orderCreatedEvent.getOrder().getUserId());
                log.info("Cart for User: {} deleted!", orderCreatedEvent.getOrder().getUserId());
            }
            log.info("Sending order email to {}", orderCreatedEvent.getOrder().getShippingAddress().getEmail());
            try {
                emailService.sendOrderEmail(orderCreatedEvent.getOrder());
                log.info("Sending order email to {} DONE", orderCreatedEvent.getOrder().getShippingAddress().getEmail());
            } catch (MessagingException e) {
                log.info("Sending order email to " + orderCreatedEvent.getOrder().getShippingAddress().getEmail() + " FAILED ", e.getMessage());
            }
        }
    }

    private void persistOrderHistory(Order order){
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrder(order);
        orderHistory.setProductCount(order.getProductCount());
        orderHistory.setTotal(order.getTotal());
        orderHistory.setState(order.getState());
        orderHistory.setDeliveryDate(order.getDeliveryDate());
        orderHistoryRepository.save(orderHistory);
    }

    public Map<String, ProductDTO> getProductDTOS(List<OrderEntry> orderEntries) {
        if (CollectionUtils.isNotEmpty(orderEntries)) {
            String productParam = orderEntries.stream()
                    .map(OrderEntry::getProductSlug)
                    .collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(productParam)) {
                return productServiceClient.callGetAllProductsFromUserFavorites(productParam, true).getProducts().stream().collect(Collectors.toMap(ProductDTO::getSlug, item->item));
            }
        }

        return new HashMap<>();
    }

    private static LocalDate addBusinessDays(LocalDate localDate, int days) {
        Predicate<LocalDate> isWeekend = date
                -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        LocalDate result = localDate;

        while (days > 0) {
            result = result.plusDays(1);
            if (isWeekend.negate().test(result)) {
                days--;
            }
        }

        return result;
    }
}
