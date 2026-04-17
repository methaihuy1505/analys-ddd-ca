package de.jenshardt.multimavendemo.customer.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.jenshardt.multimavendemo.customer.domain.aggregate.Customer;
import de.jenshardt.multimavendemo.customer.domain.service.CustomerService;

@RestController
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);
	
	@GetMapping("customers")
	public ResponseEntity<List<Customer>> getAllCustomer() {
		LOG.info("Get all customers");
		
		return new ResponseEntity<List<Customer>>(customerService.getAllCustomers(), HttpStatus.OK);
	}

	@PostMapping("customers")
	public ResponseEntity<Customer> postCustomer(@RequestParam String name, @RequestParam String job) {
		LOG.info("Create new customer with name {} and job {}", name, job);
		
		return new ResponseEntity<Customer>(customerService.createCustomer(name, job), HttpStatus.CREATED);
	}
	
	@GetMapping("customers/{customerId}")
	public ResponseEntity<Customer> getCustomer(@PathVariable UUID customerId) {
		LOG.info("Get Customer with id {}, customerId");
		
		Customer cust = customerService.getCustomerById(customerId);
		
		if (cust == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);	
		}
		
		return new ResponseEntity<Customer>(cust, HttpStatus.OK); 
	}
	
	
	@DeleteMapping("customers/{customerId}")
	public ResponseEntity<Boolean> deleteCustomer(@PathVariable UUID customerId) {
		LOG.info("Delete Customer with id {}, customerId");
		
		if (customerService.deleteCustomerById(customerId)) {
			return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
		}
			
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.NOT_FOUND);
	}
}
