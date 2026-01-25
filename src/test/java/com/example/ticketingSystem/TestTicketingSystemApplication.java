package com.example.ticketingSystem;

import org.springframework.boot.SpringApplication;

public class TestTicketingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.from(TicketingSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
