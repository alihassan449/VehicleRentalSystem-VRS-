package com.rental.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.rental.model.Customer;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> customersById = new HashMap<>();

    @Override
    public void save(Customer customer) { customersById.put(customer.getCustomerId(), customer); }

    @Override
    public Optional<Customer> findById(String customerId) { return Optional.ofNullable(customersById.get(customerId)); }

    @Override
    public List<Customer> findByNationalId(String nationalId) {
        List<Customer> result = new ArrayList<>();
        for (Customer c : customersById.values())
            if (c.getNationalId() != null && c.getNationalId().equalsIgnoreCase(nationalId)) result.add(c);
        return result;
    }

    @Override
    public List<Customer> findByNameContains(String namePart) {
        List<Customer> result = new ArrayList<>();
        if (namePart == null) return result;
        String needle = namePart.toLowerCase();
        for (Customer c : customersById.values())
            if (c.getName() != null && c.getName().toLowerCase().contains(needle)) result.add(c);
        return result;
    }

    @Override
    public List<Customer> findAll() { return new ArrayList<>(customersById.values()); }
}
