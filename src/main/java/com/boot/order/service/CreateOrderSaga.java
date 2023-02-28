package com.boot.order.service;

import com.boot.command.ReserveProductsCommand;
import com.boot.order.dto.CreateOrderSagaData;
import com.boot.command.InsufficientProductsInStock;
import com.boot.order.dto.OrderDTO;
import com.boot.order.dto.RejectionReason;
import com.boot.order.model.Order;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.eventuate.tram.sagas.simpledsl.*;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

@Service
public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {

  @Autowired
  private OrderService orderService;

  public CreateOrderSaga(OrderService orderService) {
    this.orderService = orderService;
  }

  private SagaDefinition<CreateOrderSagaData> sagaDefinition =
          step()
            .invokeLocal(this::create)
            .withCompensation(this::reject)
          .step()
            .invokeParticipant(this::reserveProducts)
            .onReply(InsufficientProductsInStock.class, this::handleInsufficientProducts)
          .step()
            .invokeLocal(this::approve)
          .build();

  private void handleInsufficientProducts(CreateOrderSagaData data, InsufficientProductsInStock reply) {
    data.setRejectionReason(RejectionReason.INSUFFICIENT_PRODUCTS);
  }

  @Override
  public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
    return this.sagaDefinition;
  }

  private void create(CreateOrderSagaData data) {
    Order order = orderService.createOrder(data.getOrderDetails());
    data.setOrderId(order.getId());
  }

  private void reject(CreateOrderSagaData data) {
    orderService.rejectOrder(data.getOrderId(), data.getRejectionReason());
  }

  private void approve(CreateOrderSagaData data) {
    orderService.approveOrder(data.getOrderId());
  }

  private CommandWithDestination reserveProducts(CreateOrderSagaData data) {
    long orderId = data.getOrderId();
    Long customerId = data.getOrderDetails().getUserId();
    OrderDTO orderDTO = data.getOrderDetails().getOrderDTO();
    return send(new ReserveProductsCommand(orderId, orderDTO, customerId))
            .to("productService")
            .build();
  }

}
