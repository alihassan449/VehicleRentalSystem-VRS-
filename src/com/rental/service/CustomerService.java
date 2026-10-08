package com.rental.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.rental.model.Address;
import com.rental.model.Customer;
import com.rental.model.DrivingLicense;
import com.rental.repository.CustomerRepository;
import com.rental.util.IdGenerator;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) { this.customerRepository = customerRepository; }

    public Customer registerCustomer(String name, Address address, String contactNumber,
                                      String nationalId, DrivingLicense drivingLicense) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Customer name is required");
        if (contactNumber == null || contactNumber.isBlank()) throw new IllegalArgumentException("Contact number is required");
        if (nationalId == null || nationalId.isBlank()) throw new IllegalArgumentException("National ID is required");
        String id = IdGenerator.next("CUS");
        Customer customer = new Customer(id, name, address, contactNumber, nationalId, drivingLicense);
        customerRepository.save(customer);
        return customer;
    }

    public List<Customer> searchCustomers(String keyword) {
        List<Customer> results = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) return results;
        customerRepository.findById(keyword).ifPresent(results::add);
        for (Customer c : customerRepository.findByNationalId(keyword)) if (!results.contains(c)) results.add(c);
        for (Customer c : customerRepository.findByNameContains(keyword)) if (!results.contains(c)) results.add(c);
        return results;
    }

    public void updateContactInfo(String customerId, String newContactNumber, Address newAddress) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("No customer with ID " + customerId));
        customer.updateContactInfo(newContactNumber, newAddress);
        customerRepository.save(customer);
    }

    public boolean isEligibleForRental(Customer customer) {
        if (customer == null || customer.getDrivingLicense() == null) return false;
        return !customer.getDrivingLicense().isExpired(LocalDate.now());
    }

    public List<Customer> findAll() { return customerRepository.findAll(); }
}
