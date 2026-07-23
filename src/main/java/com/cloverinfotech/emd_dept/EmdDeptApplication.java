package com.cloverinfotech.emd_dept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmdDeptApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmdDeptApplication.class, args);
		
		final Logger log = LoggerFactory.getLogger(EmdDeptApplication.class);
		log.info("Application started successfully");
	}
}

