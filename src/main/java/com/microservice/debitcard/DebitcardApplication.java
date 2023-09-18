package com.microservice.debitcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DebitcardApplication {

	public static void main(String[] args) {
		SpringApplication.run(DebitcardApplication.class, args);
	}

}
