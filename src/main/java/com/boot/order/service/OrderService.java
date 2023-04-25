package com.boot.order.service;

import com.boot.order.client.CartServiceClient;
import com.boot.order.client.ProductServiceClient;
import com.boot.order.dto.OrderCreatedEvent;
import com.boot.order.dto.OrderDTO;
import com.boot.order.dto.OrderEntryDTO;
import com.boot.order.dto.ProductReservedEvent;
import com.boot.order.enums.OrderStatus;
import com.boot.order.exception.EntityNotFoundException;
import com.boot.order.exception.InvalidInputDataException;
import com.boot.order.model.Order;
import com.boot.order.model.OrderEntry;
import com.boot.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
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

    private CartServiceClient cartServiceClient;

    private ProductServiceClient productServiceClient;

    private EmailService emailService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private static final ModelMapper MAPPER = new ModelMapper();

    static {
        MAPPER.typeMap(OrderEntry.class, OrderDTO.class);
    }

    @Transactional
    public OrderDTO createNewOrder(OrderDTO orderDto, String email, long userId) {
        log.info("createNewOrder - process started");
        productServiceClient.subtractProducts(orderDto.getEntries());
        log.info("Subtract products {}", orderDto.getEntries().stream()
                .map(item -> "name:" + item.getProductName() + " quantity:" + item.getQuantity())
                .collect(Collectors.joining(";")));
        applicationEventPublisher.publishEvent(new ProductReservedEvent(orderDto.getEntries()));
        Order order = new Order();
        order.setUuid(UUID.randomUUID())
                .setEmail(email)
                .setFirstName((orderDto.getFirstName()))
                .setLastName((orderDto.getLastName()))
                .setAddressLine1((orderDto.getAddressLine1()))
                .setAddressLine2((orderDto.getAddressLine2()))
                .setCity((orderDto.getCity()))
                .setState((orderDto.getState()))
                .setZipPostalCode((orderDto.getZipPostalCode()))
                .setCountry((orderDto.getCountry()))
                .setStatus(OrderStatus.IN_PROGRESS)
                .setLastUpdatedOn(LocalDateTime.now());

        List<OrderEntry> newOrderEntries = new ArrayList<>();

        double orderTotal = 0;

        for (OrderEntryDTO entry : orderDto.getEntries()) {
            OrderEntry orderEntry = new OrderEntry();

            orderEntry.setProductName(entry.getProductName());
            orderEntry.setPrice(entry.getPrice());
            orderEntry.setQuantity(entry.getQuantity());
            orderEntry.setOrder(order);

            orderTotal += entry.getPrice() * entry.getQuantity();

            newOrderEntries.add(orderEntry);
        }

        order.setEntries(newOrderEntries);
        order.setTotal(orderTotal);

        orderRepository.save(order);
        log.info("Order for User: {} saved!", email);

        cartServiceClient.deleteCartByUserId(userId);
        log.info("Cart for User: {} deleted!", email);

        applicationEventPublisher.publishEvent(new OrderCreatedEvent(order));

        return MAPPER.map(order, OrderDTO.class);
    }

    @Transactional
    public void deleteOrder(String orderId, String email) {
        log.info("Deleting order {} for user {}", orderId, email);
        Optional<Order> orderOptional = orderRepository.getFirstByUuid(UUID.fromString(orderId));
        if (orderOptional.isEmpty()) {
            throw new EntityNotFoundException("Order could not be found");
        }
        Order order = orderOptional.get();
        if (!order.getEmail().equalsIgnoreCase(email)) {
            throw new InvalidInputDataException("Order can be canceled only by the owner");
        }
        List<OrderEntryDTO> entries = order.getEntries().stream()
                .map(item -> new OrderEntryDTO(item.getProductName(), item.getPrice(), item.getQuantity()))
                .collect(Collectors.toList());
        productServiceClient.addProducts(entries);
        log.info("Add products {}", entries.stream()
                .map(item -> "name:" + item.getProductName() + " quantity:" + item.getQuantity())
                .collect(Collectors.joining(";")));
        orderRepository.delete(order);
        log.info("Deleted order {} for user {}", orderId, email);
    }

    @TransactionalEventListener(phase = AFTER_ROLLBACK)
    public void rollbackProductsReserve(ProductReservedEvent productReservedEvent) {
        if (productReservedEvent != null) {
            log.info("Add product service as compensating action for {}", productReservedEvent.getEntries().stream()
                    .map(item -> "name:" + item.getProductName() + " quantity:" + item.getQuantity())
                    .collect(Collectors.joining(";")));
            productServiceClient.addProducts(productReservedEvent.getEntries());
            log.info("Add product service as compensating action finished for {}", productReservedEvent.getEntries().stream()
                    .map(item -> "name:" + item.getProductName() + " quantity:" + item.getQuantity())
                    .collect(Collectors.joining(";")));
        }
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void commitOrderCreation(OrderCreatedEvent orderCreatedEvent) {
        if (orderCreatedEvent != null) {
            log.info("Sending order email to {}", orderCreatedEvent.getOrder().getEmail());
            try {
                emailService.sendOrderEmail(orderCreatedEvent.getOrder());
                log.info("Sending order email to {} DONE", orderCreatedEvent.getOrder().getEmail());
            } catch (MessagingException e) {
                log.info("Sending order email to " + orderCreatedEvent.getOrder().getEmail() + " FAILED ", e.getMessage());
            }
        }
    }
}
