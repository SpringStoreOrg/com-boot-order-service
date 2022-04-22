
package com.boot.order.controller;

import com.boot.services.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.boot.order.service.OrderService;
import com.boot.services.dto.OrderDTO;

@Controller
@RequestMapping("/")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/createNewOrder/{userName}")
	public ResponseEntity<OrderDTO> createNewOrder(@RequestBody OrderDTO orderDto, @PathVariable("userName") String userName) {
		OrderDTO newOrder = orderService.createNewOrder(orderDto ,userName);
		return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
	}

}
