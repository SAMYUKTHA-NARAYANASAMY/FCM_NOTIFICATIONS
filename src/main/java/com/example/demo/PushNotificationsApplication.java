package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PushNotificationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PushNotificationsApplication.class, args);
		System.out.println("main method is running");
	}

}
