package com.boot.order.client;

import com.boot.order.dto.CartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name="cart-service")
public interface CartServiceClient {
	@DeleteMapping
	ResponseEntity<CartDTO> deleteCartByUserId(@RequestHeader("User-Id") long userId);
}