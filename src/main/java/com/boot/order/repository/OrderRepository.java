package com.boot.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boot.services.model.Order;
import com.boot.services.model.OrderStatus;
import com.boot.services.model.User;



@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUser(User user);
	
	Order findOrderByUserIdAndStatus(long id, OrderStatus orderStatus);

}
