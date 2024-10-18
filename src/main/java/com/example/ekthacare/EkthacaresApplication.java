package com.example.ekthacare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.ekthacare.entity")
public class EkthacaresApplication {

	public static void main(String[] args) {
		SpringApplication.run(EkthacaresApplication.class, args);
		System.out.println("Demo application");
	}

}
