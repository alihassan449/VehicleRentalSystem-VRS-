package com.rental.repository;

import java.util.List;
import java.util.Optional;

import com.rental.model.Customer;

public interface CustomerRepository {
    void save(Customer customer);
    Optional<Customer> findById(String customerId);
    List<Customer> findByNationalId(String nationalId);
    List<Customer> findByNameContains(String namePart);
    List<Customer> findAll();
}
