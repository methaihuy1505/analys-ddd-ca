package de.jenshardt.multimavendemo.customer.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.jenshardt.multimavendemo.customer.domain.aggregate.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

}
