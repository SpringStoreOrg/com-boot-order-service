
package com.boot.order.controller;

import com.boot.order.dto.OrderDTO;
import com.boot.order.service.OrderService;
import com.boot.order.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@Controller
@RequestMapping("/")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> createNewOrder(@Valid @RequestBody OrderDTO orderDto,
                                                   @RequestHeader(value = "Username", required = false) @Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) String email,
                                                   @RequestHeader(value = "User-Id", required = false) Long userId) {
        String orderId = orderService.createNewOrder(orderDto, email, userId);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Object> cancelOrder(@PathVariable("orderId") String orderId) {
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
