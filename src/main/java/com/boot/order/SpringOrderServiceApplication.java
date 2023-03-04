package com.boot.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableEurekaClient
@EntityScan(basePackages = {"com.boot.order.model",
		"org.axonframework.modelling.saga.repository.jpa",
		"org.axonframework.eventhandling.tokenstore.jpa"})
public class SpringOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringOrderServiceApplication.class, args);
		log.info("Spring Order Services Application with rabbitmq started successfully.");
	}
}
