package com.ramblealot.localsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LocalsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalsearchApplication.class, args);
	}

}
