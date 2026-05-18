package com.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EDocumentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EDocumentServiceApplication.class, args);
	}

}
