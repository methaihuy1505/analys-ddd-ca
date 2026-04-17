package de.jenshardt.multimavendemo.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApplication {

	private static final Logger LOG = LoggerFactory.getLogger(AuthApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
		
		LOG.info("AuthApplication Started........");
	}
}