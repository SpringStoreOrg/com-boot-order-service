package com.boot.order.rabbitmq;

import com.boot.order.model.Order;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Producer {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	Binding binding;

	public void produce(Order order) {

		log.info("Storing notification...");
		rabbitTemplate.convertAndSend(binding.getExchange(), binding.getRoutingKey(), order);
		log.info("Notification stored in queue sucessfully");

	}
}
