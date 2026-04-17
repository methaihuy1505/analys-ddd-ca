package de.jenshardt.multimavendemo.customer.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.jenshardt.multimavendemo.customer.domain.aggregate.Customer;
import de.jenshardt.multimavendemo.customer.domain.repository.CustomerRepository;
import de.jenshardt.multimavendemo.customer.domain.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepo;

	@Override
	public Customer getCustomerById(UUID customerId) {
		Optional<Customer> cust = customerRepo.findById(customerId);

		if (cust.isPresent()) {
			return cust.get();
		}

		return null;
	}

	@Override
	public Customer createCustomer(String name, String job) {
		Customer cust = customerRepo.save(new Customer(name, job));

		return cust;
	}

	@Override
	public List<Customer> getAllCustomers() {
		List<Customer> findAll = customerRepo.findAll();

		return findAll;
	}

	@Override
	public boolean deleteCustomerById(UUID customerId) {
		Optional<Customer> cust = customerRepo.findById(customerId);

		if (cust.isPresent()) {
			customerRepo.deleteById(customerId);
			return true;
		}
		
		return false;
	}
}
