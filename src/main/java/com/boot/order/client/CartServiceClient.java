package com.boot.order.client;

import com.boot.order.dto.CartDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@AllArgsConstructor
public class CartServiceClient {

	private RestTemplate cartServiceRestTemplate;

	public CartDTO callGetCartByUserId(long userId) {

		return cartServiceRestTemplate.exchange("/", HttpMethod.GET, new HttpEntity<String>(getRequestHeaders(userId)), CartDTO.class)
				.getBody();
	}

	public void callDeleteCartByUserId(long userId) {

		cartServiceRestTemplate.exchange("/", HttpMethod.DELETE,
				new HttpEntity<String>(getRequestHeaders(userId)), String.class);
	}

	private HttpHeaders getRequestHeaders(long userId){
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Id", String.valueOf(userId));

		return headers;
	}
}