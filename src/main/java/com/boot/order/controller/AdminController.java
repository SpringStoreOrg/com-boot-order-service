package com.boot.order.controller;

import com.boot.order.dto.OrderGetDTO;
import com.boot.order.dto.OrderSendDTO;
import com.boot.order.service.OrderService;
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
import java.util.List;

@Validated
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<OrderGetDTO>> getOrdersByAdmin(@RequestHeader(value = "User-Id") Long userId, @PageableDefault(size = 10, direction = Sort.Direction.DESC, sort = {"createdOn"}) Pageable pageable) {
        return new ResponseEntity<>(orderService.getOrders(pageable), HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity cancelOrder(@PathVariable("orderId") String orderId, @RequestHeader(value = "User-Id") Long userId) {
        orderService.cancelOrderByAdmin(orderId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{orderId}/sent")
    public ResponseEntity markAsSent(@PathVariable("orderId") String orderId,
                                                      @Valid @RequestBody OrderSendDTO orderSent,
                                                      @RequestHeader(value = "User-Id") Long userId) {
        orderService.markAsSent(orderId,orderSent, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{orderId}/completed")
    public ResponseEntity<OrderGetDTO> markAsCompleted(@PathVariable("orderId") String orderId,
                                                  @RequestHeader(value = "User-Id") Long userId) {
        orderService.markAsCompleted(orderId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
