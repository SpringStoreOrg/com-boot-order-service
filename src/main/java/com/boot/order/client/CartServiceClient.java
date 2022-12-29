package com.boot.order.client;

import com.boot.order.dto.CartDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;


@Component
@AllArgsConstructor
public class CartServiceClient {

	private RestTemplate cartServiceRestTemplate;

	public CartDTO callGetCartByUserId(long userId) {

		return cartServiceRestTemplate.exchange("/", HttpMethod.GET, new HttpEntity<Object>(getRequestHeaders(userId)), CartDTO.class)
				.getBody();
	}

	public void callDeleteCartByUserId(long userId) {

		cartServiceRestTemplate.exchange("/", HttpMethod.DELETE,
				new HttpEntity<>(getRequestHeaders(userId)), String.class);
	}

	private Map<String, String> getRequestHeaders(long userId){
		Map<String, String> headers = new HashMap<>();
		headers.put("User-Id", String.valueOf(userId));

		return headers;
	}
}