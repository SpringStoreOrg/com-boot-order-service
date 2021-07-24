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


	public OrderDTO createNewOrder(String userName) {

		log.info("createNewOrder - process started");

		Cart cart = CartMapper.dtoToCartEntity(cartServiceClient.callGetCartByUserName(userName));

		Order order = new Order();
		order.setUuid(UUID.randomUUID());
		order.setStatus(OrderStatus.IN_PROGRESS);
		order.setProductList(cart.getProductList());
		order.setUser(cart.getUser());
		order.setTotal(cart.getTotal());
		order.setLastUpdatedOn(LocalDateTime.now());

		orderRepository.save(order);
		log.info("Order for User: " + userName + " saved!");

		cartServiceClient.callDeleteCartByUserName(userName);
		log.info("Cart for User: " + userName + " deleted!");
		
		producer.produce(order);

		return OrderMapper.orderEntityToDto(order);
	}

}
