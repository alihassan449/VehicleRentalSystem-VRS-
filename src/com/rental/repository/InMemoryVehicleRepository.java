package com.rental.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.rental.enums.VehicleCategory;
import com.rental.enums.VehicleStatus;
import com.rental.model.Vehicle;

public class InMemoryVehicleRepository implements VehicleRepository {
    private final Map<String, Vehicle> vehiclesByRegistration = new HashMap<>();

    @Override
    public void save(Vehicle vehicle) { vehiclesByRegistration.put(vehicle.getRegistrationNumber(), vehicle); }

    @Override
    public Optional<Vehicle> findByRegistrationNumber(String registrationNumber) {
        return Optional.ofNullable(vehiclesByRegistration.get(registrationNumber));
    }

    @Override
    public List<Vehicle> findByCategory(VehicleCategory category) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehiclesByRegistration.values()) if (v.getCategory() == category) result.add(v);
        return result;
    }

    @Override
    public List<Vehicle> findByStatus(VehicleStatus status) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehiclesByRegistration.values()) if (v.getStatus() == status) result.add(v);
        return result;
    }

    @Override
    public List<Vehicle> findAll() { return new ArrayList<>(vehiclesByRegistration.values()); }
}
