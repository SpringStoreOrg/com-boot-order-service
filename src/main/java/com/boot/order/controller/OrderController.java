
package com.boot.order.controller;

import com.boot.order.dto.OrderDTO;
import com.boot.order.dto.OrderGetDTO;
import com.boot.order.dto.OrderGetDetailsDTO;
import com.boot.order.service.OrderService;
import com.boot.order.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;

@Validated
@Controller
@RequestMapping("/")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderGetDTO> createNewOrder(@Valid @RequestBody OrderDTO orderDto,
                                                   @RequestHeader(value = "Username", required = false) @Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) String email,
                                                   @RequestHeader(value = "User-Id", required = false) Long userId) {
        OrderGetDTO order = orderService.createNewOrder(orderDto, email, userId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity cancelOrder(@PathVariable("orderId") String orderId, @RequestHeader(value = "User-Id") Long userId) {
        orderService.cancelOrderByUser(orderId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<OrderGetDTO>> getOrdersForUser(@RequestHeader(value = "User-Id") Long userId, @PageableDefault(size = 10, direction = Sort.Direction.DESC, sort = {"createdOn"}) Pageable pageable) {
        return new ResponseEntity<>(orderService.getOrders(userId, pageable), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    @ResponseBody
    public ResponseEntity<OrderGetDetailsDTO> getOrder(@PathVariable("orderId") String orderId) {
        return new ResponseEntity<>(orderService.getOrder(orderId), HttpStatus.OK);
    }
}
