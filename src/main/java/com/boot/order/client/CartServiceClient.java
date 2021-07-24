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
	private RestTemplate restTemplate;


	public CartDTO callGetCartByUserName(String userName) {

		return restTemplate.getForEntity(Constants.GET_CART_BY_USER_NAME + userName, CartDTO.class).getBody();
	}
	
	public void callDeleteCartByUserName(String userName) {

		restTemplate.exchange(Constants.DELETE_CART_BY_USER_NAME + userName, HttpMethod.DELETE,
				new HttpEntity<>(UserDTO.class), String.class);
	}
}