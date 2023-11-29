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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.AFTER_ROLLBACK;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;

    private OrderHistoryRepository orderHistoryRepository;

    private CartServiceClient cartServiceClient;

    private ProductServiceClient productServiceClient;

    private EmailService emailService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private ModelMapper modelMapper;

    @Transactional
    public String createNewOrder(OrderDTO orderDto, String email, Long userId) {
        log.info("Subtract products {}", orderDto.getEntries().stream()
                .map(item -> String.format("name:%s quantity:%s",  item.getProductSlug(), item.getQuantity()))
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
            Optional<StockDTO> optionalStockDTO = reserveCallResult.stream().filter(item -> item.getProductSlug().equals(entry.getProductSlug())).findFirst();
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

        orderRepository.save(order);

        persistOrderHistory(order);

        log.info("Order for User: {} saved!", email);

        applicationEventPublisher.publishEvent(new OrderCreatedEvent(order));

        return order.getUuid().toString();
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
                .map(item -> String.format("name:%s quantity:%s", item.getProductSlug(),  item.getQuantity()))
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

        return modelMapper.map(orderOptional.get(), OrderGetDetailsDTO.class);
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
        orderHistoryRepository.save(orderHistory);
    }
}
