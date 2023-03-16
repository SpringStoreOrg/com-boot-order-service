package com.boot.order.service;

import com.boot.command.CompleteOrderCommand;
import com.boot.command.ReserveProductsCommand;
import com.boot.event.OrderCancelledEvent;
import com.boot.event.OrderCompletedEvent;
import com.boot.event.OrderCreatedEvent;
import com.boot.event.ProductsReservedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class OrderProcessingSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    public OrderProcessingSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    private void handle(OrderCreatedEvent event) {
        // we can use QueryGateway to get the details from other service.
        // Added the detail we can integrate other service for fetching the detail
        ReserveProductsCommand reserveProductsCommand  = ReserveProductsCommand.builder()
                .entries(event.getEntries())
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .email(event.getEmail())
                .build();

        commandGateway.send(reserveProductsCommand);
        log.info("{} OrderCreatedEvent handled", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductsReservedEvent event) {

        CompleteOrderCommand completeOrderCommand  = CompleteOrderCommand.builder()
                .orderId(event.getOrderId())
                .build();

        commandGateway.send(completeOrderCommand);
        log.info("{} ProductsReservedEvent handled", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCompletedEvent event) {
        log.info("{} OrderCompletedEvent handled", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCancelledEvent event) {
        log.info("{} OrderCancelledEvent handled", event.getOrderId());
    }
}
