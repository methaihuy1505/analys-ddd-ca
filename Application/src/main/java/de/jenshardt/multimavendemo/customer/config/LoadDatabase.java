package de.jenshardt.multimavendemo.customer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.jenshardt.multimavendemo.customer.domain.service.CustomerService;

@Configuration
class LoadDatabase {

	private static final Logger LOG = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(CustomerService service) {

		return args -> {
			LOG.info("Preloading {}", service.createCustomer("Bilbo Baggins", "burglar"));
			LOG.info("Preloading {}", service.createCustomer("Frodo Baggins", "thief"));
		};
	}
}