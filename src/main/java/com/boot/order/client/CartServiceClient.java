package com.boot.order.client;

import com.boot.order.dto.CartDTO;
import com.boot.order.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.boot.order.util.Constants;


@Component
@AllArgsConstructor
public class CartServiceClient {

	private RestTemplate cartServiceRestTemplate;

	public CartDTO callGetCartByEmail(String email) {

		return cartServiceRestTemplate.getForEntity(Constants.GET_CART_BY_EMAIL, CartDTO.class, email).getBody();
	}

	public void callDeleteCartByEmail(String email) {

		cartServiceRestTemplate.exchange(Constants.DELETE_CART_BY_EMAIL, HttpMethod.DELETE,
				new HttpEntity<>(UserDTO.class), String.class, email);
	}
}