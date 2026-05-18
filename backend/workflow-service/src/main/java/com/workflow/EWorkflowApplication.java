package com.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EWorkflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(EWorkflowApplication.class, args);
	}

}
