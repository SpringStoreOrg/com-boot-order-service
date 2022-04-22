package com.boot.order.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.boot.order.util.Constants;
import com.boot.services.dto.CartDTO;
import com.boot.services.dto.UserDTO;

public class CartServiceClient {


	@Autowired
	private RestTemplate cartServiceRestTemplate;

	public CartDTO callGetCartByEmail(String email) {

		return cartServiceRestTemplate.getForEntity(Constants.GET_CART_BY_EMAIL, CartDTO.class, email).getBody();
	}

	public void callDeleteCartByEmail(String email) {

		cartServiceRestTemplate.exchange(Constants.DELETE_CART_BY_EMAIL, HttpMethod.DELETE,
				new HttpEntity<>(UserDTO.class), String.class, email);
	}
}