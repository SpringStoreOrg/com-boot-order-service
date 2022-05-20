
package com.boot.order.controller;

import com.boot.order.util.Constants;
import com.boot.services.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.boot.order.service.OrderService;
import com.boot.services.dto.OrderDTO;

import javax.validation.constraints.Email;

@Controller
@RequestMapping("/")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping
	@ResponseBody
	public ResponseEntity<OrderDTO> createNewOrder(@RequestBody OrderDTO orderDto, @Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @RequestParam String email) {
		OrderDTO newOrder = orderService.createNewOrder(orderDto ,email);
		return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
	}

}
