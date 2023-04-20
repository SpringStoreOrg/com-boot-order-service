package com.boot.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EntityScan(basePackages = { "com.boot.order.model" })
public class SpringOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringOrderServiceApplication.class, args);
		log.info("Spring Order Services Application with rabbitmq started successfully.");
	}
}
