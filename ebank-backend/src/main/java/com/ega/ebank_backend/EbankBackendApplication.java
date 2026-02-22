package com.ega.ebank_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EbankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankBackendApplication.class, args);
	}

}
