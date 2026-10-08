package com.rental.repository;

import java.util.List;
import java.util.Optional;

import com.rental.enums.VehicleCategory;
import com.rental.enums.VehicleStatus;
import com.rental.model.Vehicle;

public interface VehicleRepository {
    void save(Vehicle vehicle);
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    List<Vehicle> findByCategory(VehicleCategory category);
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findAll();
}
