package com.boot.order.service;


import com.boot.command.CancelOrderCommand;
import com.boot.command.ReserveProductsCommand;
import com.boot.event.OrderCancelledEvent;
import com.boot.event.OrderCompletedEvent;
import com.boot.event.OrderCreatedEvent;
import com.boot.command.CreateOrderCommand;
import com.boot.order.dto.OrderEntryDTO;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.UUID;

@Aggregate
@Slf4j
public class OrderAggregate {
    @AggregateIdentifier
    private UUID orderId;
    private String email;
    private long userId;
    private String orderStatus;
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipPostalCode;
    private String country;
    private List<OrderEntryDTO> entries;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("CreateOrderCommand in Saga for Order Id : {}", command.getOrderId());
        //Validate The Command
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(command, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent in Saga for Order Id : {}", event.getOrderId());
        this.userId = event.getUserId();
        this.orderId = event.getOrderId();
    }

    @CommandHandler
    public void handle(ReserveProductsCommand reserveProductsCommand) {
        log.info("ReserveProductsCommand in Saga for Order Id : {}", reserveProductsCommand.getOrderId());
        //Validate The Command
        // Publish Order Completed Event
        OrderCompletedEvent orderCompletedEvent = OrderCompletedEvent.builder()
                .orderId(reserveProductsCommand.getOrderId())
                .userId(reserveProductsCommand.getUserId())
                .email(reserveProductsCommand.getEmail())
                .build();

        AggregateLifecycle.apply(orderCompletedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {
        log.info("OrderCompletedEvent in Saga for Order Id : {}", event.getOrderId());
        //this.orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        log.info("CancelOrderCommand in Saga for Order Id : {}", command.getOrderId());
        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent();
        BeanUtils.copyProperties(command, orderCancelledEvent);
        AggregateLifecycle.apply(orderCancelledEvent);
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        log.info("OrderCancelledEvent in Saga for Order Id : {}", event.getOrderId());
        //this.orderStatus = event.getOrderStatus();
    }
}
