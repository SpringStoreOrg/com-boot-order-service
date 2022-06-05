package com.boot.order.repository;

import com.boot.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;





@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
