package com.boot.order.service;

import com.boot.event.OrderCancelledEvent;
import com.boot.event.OrderCompletedEvent;
import com.boot.event.OrderCreatedEvent;
import com.boot.order.client.CartServiceClient;
import com.boot.order.dto.OrderDTO;
import com.boot.order.dto.OrderEntryDTO;
import com.boot.order.model.Order;
import com.boot.order.model.OrderEntry;
import com.boot.order.model.OrderStatus;
import com.boot.order.model.RejectionReason;
import com.boot.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    //https://blog.knoldus.com/saga-axonframework-springboot-1/
    //https://blog.knoldus.com/saga-axonframework-springboot-2/
    private OrderRepository orderRepository;

    private CartServiceClient cartServiceClient;

    private static final ModelMapper MAPPER = new ModelMapper();

    static{
        MAPPER.typeMap(OrderEntry.class, OrderDTO.class);
    }

    @Transactional
    public OrderDTO getOrderById(Long orderId){
        return orderRepository.findById(orderId)
                .map(value -> MAPPER.map(value, OrderDTO.class))
                .orElse(null);
    }

    @EventHandler
    @Transactional
    public void on(OrderCreatedEvent event) {
        log.info("createNewOrder after OrderCreatedEvent process started");
        Order order = new Order();
        order.setUuid(event.getOrderId())
                .setEmail(event.getEmail())
                .setFirstName((event.getFirstName()))
                .setLastName((event.getLastName()))
                .setAddressLine1((event.getAddressLine1()))
                .setAddressLine2((event.getAddressLine2()))
                .setCity((event.getCity()))
                .setState((event.getState()))
                .setZipPostalCode((event.getZipPostalCode()))
                .setCountry((event.getCountry()))
                .setStatus(OrderStatus.RECEIVED)
                .setLastUpdatedOn(LocalDateTime.now());

        List<OrderEntry> newOrderEntries = new ArrayList<>();

        double orderTotal = 0;

        for (OrderEntryDTO entry : event.getEntries()) {
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
        log.info("Order for User: {} saved!", event.getEmail());
        log.info("{} createNewOrder COMPLETED", event.getOrderId());
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        Order order = orderRepository.getFirstByUuid(event.getOrderId());
        order.approve();
        orderRepository.save(order);
        log.info("{} OrderCompletedEvent completed", event.getOrderId());
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        Order order = orderRepository.getFirstByUuid(event.getOrderId());
        order.reject(RejectionReason.valueOf(event.getRejectionReason()));
        orderRepository.save(order);
        log.info("{} OrderCancelledEvent completed", event.getOrderId());
    }}
