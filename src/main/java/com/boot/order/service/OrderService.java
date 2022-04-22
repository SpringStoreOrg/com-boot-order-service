package com.boot.order.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.order.client.CartServiceClient;
import com.boot.order.rabbitmq.Producer;
import com.boot.order.repository.OrderRepository;
import com.boot.services.dto.OrderDTO;
import com.boot.services.mapper.CartMapper;
import com.boot.services.mapper.OrderMapper;
import com.boot.services.model.Cart;
import com.boot.services.model.Order;
import com.boot.services.model.OrderStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartServiceClient cartServiceClient;

	@Autowired
	private Producer producer;


	public OrderDTO createNewOrder(OrderDTO orderDto ,String email) {

		log.info("createNewOrder - process started");

		Cart cart = CartMapper.dtoToCartEntity(cartServiceClient.callGetCartByEmail(email));

		Order order = new Order();

        order.setUuid(UUID.randomUUID())
                .setFirstName((orderDto.getFirstName()))
                .setLastName((orderDto.getLastName()))
                .setAddressLine1((orderDto.getAddressLine1()))
                .setAddressLine2((orderDto.getAddressLine2()))
                .setCity((orderDto.getCity()))
                .setState((orderDto.getState()))
                .setZipPostalCode((orderDto.getZipPostalCode()))
                .setCountry((orderDto.getCountry()))
                .setNameOnCard((orderDto.getNameOnCard()))
                .setCardNumber((orderDto.getCardNumber()))
                .setExpiryDate((orderDto.getExpiryDate()))
                .setCvv((orderDto.getCvv()))
                .setStatus(OrderStatus.IN_PROGRESS)
                .setProductList(cart.getProductList())
                .setUser(cart.getUser())
                .setTotal(cart.getTotal())
                .setLastUpdatedOn(LocalDateTime.now());

		orderRepository.save(order);
		log.info("Order for User: {} saved!", email);

		cartServiceClient.callDeleteCartByEmail(email);
		log.info("Cart for User: {} deleted!", email);

		producer.produce(order);

		return OrderMapper.orderEntityToDto(order);
	}

}
