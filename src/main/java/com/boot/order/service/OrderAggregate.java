package com.boot.order.service;


import com.boot.command.CancelOrderCommand;
import com.boot.command.CompleteOrderCommand;
import com.boot.command.CreateOrderCommand;
import com.boot.event.OrderCancelledEvent;
import com.boot.event.OrderCompletedEvent;
import com.boot.event.OrderCreatedEvent;
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
    private UUID orderId;
    private String email;
    private long userId;
    private String orderStatus;
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String zipPostalCode;
    private String country;
    private List<OrderEntryDTO> entries;
    private OrderState state;

    public OrderAggregate(){
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        this.orderId = command.getOrderId();
        this.state = OrderState.CREATED;
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(command.getOrderId())
                .userId(command.getUserId())
                .email(command.getEmail())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .addressLine1(command.getAddressLine1())
                .addressLine2(command.getAddressLine2())
                .city(command.getCity())
                .state(command.getState())
                .zipPostalCode(command.getZipPostalCode())
                .country(command.getCountry())
                .entries(command.getEntries())
                .build();
        AggregateLifecycle.apply(orderCreatedEvent);
        log.info("{} OrderCreatedEvent trigger", command.getOrderId());
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.userId = event.getUserId();
        this.orderId = event.getOrderId();
        log.info("{} OrderCreatedEvent handler", event.getOrderId());
    }

    @CommandHandler
    public void handle(CompleteOrderCommand completeOrderCommand) {
        if(this.state == OrderState.CREATED){
            // Publish Order Completed Event
            OrderCompletedEvent orderCompletedEvent = OrderCompletedEvent.builder()
                    .orderId(completeOrderCommand.getOrderId())
                    .userId(completeOrderCommand.getUserId())
                    .email(completeOrderCommand.getEmail())
                    .build();

            AggregateLifecycle.apply(orderCompletedEvent);
            log.info("{} CompleteOrderCommand handler", completeOrderCommand.getOrderId());
        }else{
            log.info("{} CompleteOrderCommand already handled", completeOrderCommand.getOrderId());
        }
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {
        this.state = OrderState.COMPLETED;
        log.info("{} OrderCompletedEvent handler", event.getOrderId());
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        if(this.state == OrderState.CREATED){
            OrderCancelledEvent orderCancelledEvent = OrderCancelledEvent.builder()
                    .orderId(command.getOrderId())
                    .userId(command.getUserId())
                    .email(command.getEmail())
                    .rejectionReason(command.getRejectionReason())
                    .build();
            BeanUtils.copyProperties(command, orderCancelledEvent);
            AggregateLifecycle.apply(orderCancelledEvent);
            log.info("{} CancelOrderCommand handler", command.getOrderId());
        }else{
            log.info("{} CancelOrderCommand already handled", command.getOrderId());
        }
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.state = OrderState.CANCELLED;
        log.info("{} OrderCancelledEvent handler", event.getOrderId());
    }

    @Override
    @AggregateIdentifier
    public String toString() {
        return orderId + "-order";
    }

    enum OrderState{
        CREATED, COMPLETED, CANCELLED;
    }
}
