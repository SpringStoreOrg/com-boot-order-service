package com.boot.order.util;

public class Constants {

	public static final String GET_CART_BY_EMAIL = "/getCartByEmail?email={email}";
	public static final String DELETE_CART_BY_EMAIL = "/deleteCartByEmail?email={email}";

	//Regular expression used for email validation
	public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
	
}
