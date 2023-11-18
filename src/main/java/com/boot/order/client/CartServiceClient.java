package com.boot.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name="cart-service")
public interface CartServiceClient {
	@DeleteMapping
	ResponseEntity deleteCartByUserId(@RequestHeader("User-Id") long userId);
}