package com.example.pozoriste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("model")
public class PozoristeWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(PozoristeWebApplication.class, args);
	}

}
