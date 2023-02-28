package com.boot.order.service;

import com.boot.order.client.CartServiceClient;
import com.boot.order.dto.OrderDTO;
import com.boot.order.dto.OrderDetails;
import com.boot.order.dto.OrderEntryDTO;
import com.boot.order.dto.RejectionReason;
import com.boot.order.model.OrderStatus;
import com.boot.order.model.Order;
import com.boot.order.model.OrderEntry;
import com.boot.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    private static final ModelMapper MAPPER = new ModelMapper();

    static{
        MAPPER.typeMap(OrderEntry.class, OrderDTO.class);
    }

    @Transactional
    public Order createOrder(OrderDetails orderDetails){
        log.info("createNewOrder - process started");

        Order order = new Order();
        OrderDTO orderDTO = orderDetails.getOrderDTO();
        order.setUuid(UUID.randomUUID())
                .setEmail(orderDetails.getUserEmail())
                .setFirstName((orderDTO.getFirstName()))
                .setLastName((orderDTO.getLastName()))
                .setAddressLine1((orderDTO.getAddressLine1()))
                .setAddressLine2((orderDTO.getAddressLine2()))
                .setCity((orderDTO.getCity()))
                .setState((orderDTO.getState()))
                .setZipPostalCode((orderDTO.getZipPostalCode()))
                .setCountry((orderDTO.getCountry()))
                .setStatus(OrderStatus.RECEIVED)
                .setLastUpdatedOn(LocalDateTime.now());

        List<OrderEntry> newOrderEntries = new ArrayList<>();

        double orderTotal = 0;

        for (OrderEntryDTO entry : orderDTO.getEntries()) {
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
        log.info("Order for User: {} saved!", orderDetails.getUserEmail());

        return order;
    }

    @Transactional
    public Order rejectOrder(Long orderId, RejectionReason rejectionReason){
        Order order = orderRepository.getReferenceById(orderId);
        order.reject(rejectionReason);

        return order;
    }

    @Transactional
    public Order approveOrder(Long orderId){
        Order order = orderRepository.getReferenceById(orderId);
        order.approve();

        return order;
    }

    @Transactional
    public OrderDTO getOrderById(Long orderId){
        return orderRepository.findById(orderId)
                .map(value -> MAPPER.map(value, OrderDTO.class))
                .orElse(null);
    }

    @Transactional
    public OrderDTO createNewOrder(OrderDTO orderDto, String email, long userId){
    	log.info("createNewOrder - process started");

    	//batch buy based on orderDTO
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
                .setStatus(OrderStatus.RECEIVED)
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

        cartServiceClient.callDeleteCartByUserId(userId);
        log.info("Cart for User: {} deleted!", email);

        return MAPPER.map(order, OrderDTO.class);
    }
}
