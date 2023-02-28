package com.boot.order.service;

import com.boot.order.dto.CreateOrderSagaData;
import com.boot.order.dto.OrderDTO;
import com.boot.order.dto.OrderDetails;
import com.boot.order.model.Order;
import com.boot.order.repository.OrderRepository;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class OrderSagaService {
    private OrderService orderService;
    private SagaInstanceFactory sagaInstanceFactory;
    private CreateOrderSaga createOrderSaga;

    @Transactional
    public OrderDTO createOrder(OrderDetails orderDetails) {
        CreateOrderSagaData data = new CreateOrderSagaData(orderDetails);
        sagaInstanceFactory.create(createOrderSaga, data);
        return orderService.getOrderById(data.getOrderId());
    }
}
