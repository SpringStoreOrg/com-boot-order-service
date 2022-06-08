package com.boot.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.boot.order.dto.CartDTO;
import com.boot.order.dto.CartEntryDTO;
import com.boot.order.dto.OrderDTO;
import com.boot.order.enums.OrderStatus;
import com.boot.order.exception.EntityNotFoundException;
import com.boot.order.model.Order;
import com.boot.order.model.OrderEntry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.boot.order.client.CartServiceClient;
import com.boot.order.rabbitmq.Producer;
import com.boot.order.repository.OrderRepository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import static com.boot.order.model.Order.orderEntityToDto;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

	private OrderRepository orderRepository;

	private CartServiceClient cartServiceClient;

	private Producer producer;

    @Transactional
	public OrderDTO createNewOrder(OrderDTO orderDto , String email) throws EntityNotFoundException {

		log.info("createNewOrder - process started");

		CartDTO cart = cartServiceClient.callGetCartByEmail(email);

		if (cart != null) {

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
					.setTotal(cart.getTotal())
					.setLastUpdatedOn(LocalDateTime.now());

			List<OrderEntry> newOrderEntries = new ArrayList<>();

			for (CartEntryDTO cartEntry : cart.getEntries()) {
				OrderEntry orderEntry = new OrderEntry();

				orderEntry.setProductName(cartEntry.getProductName());
				orderEntry.setPrice(cartEntry.getPrice());
				orderEntry.setQuantity(cartEntry.getQuantity());
				orderEntry.setOrder(order);

				newOrderEntries.add(orderEntry);
			}

			order.setEntries(newOrderEntries);


			orderRepository.save(order);
			log.info("Order for User: {} saved!", email);

			cartServiceClient.callDeleteCartByEmail(email);
			log.info("Cart for User: {} deleted!", email);

			producer.produce(order);

			return orderEntityToDto(order);
		} else {
			throw new EntityNotFoundException("Cart not found in the Database!");
		}
	}
}
