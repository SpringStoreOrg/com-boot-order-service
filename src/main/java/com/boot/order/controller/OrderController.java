
package com.boot.order.controller;

import com.boot.command.CreateOrderCommand;
import com.boot.order.dto.OrderDTO;
import com.boot.order.service.OrderService;
import com.boot.order.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.util.UUID;

@Controller
@RequestMapping("/")
@AllArgsConstructor
@Slf4j
public class OrderController {
	private OrderService orderService;

	private CommandGateway commandGateway;

	@PostMapping
	@ResponseBody
	public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDto,
							  @RequestHeader(value = "Username") @Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) String email,
							  @RequestHeader(value = "User-Id") long userId){

		log.info("Create order endpoint called for username:{}", email);
		UUID orderId = UUID.randomUUID();
		orderDto.setUuid(orderId);

		CreateOrderCommand createOrderCommand  = CreateOrderCommand.builder()
				.orderId(orderId)
				.userId(userId)
				.email(email)
				.firstName(orderDto.getFirstName())
				.lastName(orderDto.getLastName())
				.addressLine1(orderDto.getAddressLine1())
				.addressLine2(orderDto.getAddressLine2())
				.city(orderDto.getCity())
				.state(orderDto.getState())
				.zipPostalCode(orderDto.getZipPostalCode())
				.country(orderDto.getCountry())
				.entries(orderDto.getEntries())
				.build();

		log.info("{} Sending CreateOrderCommand to gateway", createOrderCommand.getOrderId());
		commandGateway.send(createOrderCommand);

		return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
	}

}
