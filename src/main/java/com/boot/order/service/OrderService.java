package com.boot.order.service;

import com.boot.order.client.CartServiceClient;
import com.boot.order.client.ProductServiceClient;
import com.boot.order.dto.OrderDTO;
import com.boot.order.dto.OrderEntryDTO;
import com.boot.order.enums.OrderStatus;
import com.boot.order.exception.InvalidInputDataException;
import com.boot.order.model.Order;
import com.boot.order.model.OrderEntry;
import com.boot.order.rabbitmq.Producer;
import com.boot.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;

    private CartServiceClient cartServiceClient;

    private ProductServiceClient productServiceClient;

    private Producer producer;

    private static final ModelMapper MAPPER = new ModelMapper();

    static{
        MAPPER.typeMap(OrderEntry.class, OrderDTO.class);
    }

    @Transactional
    public OrderDTO createNewOrder(OrderDTO orderDto, String email, long userId){
    	log.info("createNewOrder - process started");
    	productServiceClient.subtractProducts(orderDto.getEntries());

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

        producer.produce(order);

        return MAPPER.map(order, OrderDTO.class);
    }
}
