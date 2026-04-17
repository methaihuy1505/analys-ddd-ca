package de.jenshardt.multimavendemo.customer.domain.aggregate;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Person")
public class Customer {
	
    @Id
    @GeneratedValue
	private UUID id;
    
    @Column(name = "name")
	private String name;
    
    @Column(name = "job")
    private String job;
    
    public Customer() { }
	
	public Customer(String name, String job) {
		this.name = name;
		this.job = job;
	}
	
	public Customer(UUID id, String name, String job) {
		this.id = id;
		this.name = name;
		this.job = job;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", job=" + job + "]";
	}
}
