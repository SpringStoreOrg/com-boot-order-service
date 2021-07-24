
package com.boot.order.rabbitmq;

import javax.mail.MessagingException;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boot.order.service.EmailService;
import com.boot.services.model.Order;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Consumer {

	@Autowired
	private EmailService emailService;

	@Autowired
	Queue queue;

	@RabbitListener(queues = "#{queue.getName()}") // Dynamically reading the queue name using SpEL from the "queue"
													// object.
	public void receive(Order order) {
		try {
			emailService.sendOrderEmail(order);
			log.info("Email succesfully send to following Email: " + order.getUser().getEmail());
		} catch (MessagingException e) {
			log.warn("Unable to send Email: " + e);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
