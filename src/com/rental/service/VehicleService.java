package com.rental.service;

import java.util.List;

import com.rental.enums.FuelType;
import com.rental.enums.TransmissionType;
import com.rental.enums.VehicleCategory;
import com.rental.enums.VehicleStatus;
import com.rental.model.Vehicle;
import com.rental.repository.VehicleRepository;

public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) { this.vehicleRepository = vehicleRepository; }

    public Vehicle registerVehicle(String registrationNumber, String make, String model, int year,
                                    VehicleCategory category, TransmissionType transmissionType,
                                    FuelType fuelType, double currentMileage, double dailyRentalRate) {
        if (registrationNumber == null || registrationNumber.isBlank())
            throw new IllegalArgumentException("Registration number is required");
        if (vehicleRepository.findByRegistrationNumber(registrationNumber).isPresent())
            throw new IllegalArgumentException("Vehicle " + registrationNumber + " is already registered");
        Vehicle vehicle = new Vehicle(registrationNumber, make, model, year, category,
                transmissionType, fuelType, currentMileage, dailyRentalRate);
        vehicleRepository.save(vehicle);
        return vehicle;
    }

    public List<Vehicle> searchByRegistrationNumber(String registrationNumber) {
        return vehicleRepository.findByRegistrationNumber(registrationNumber).map(List::of).orElseGet(List::of);
    }

    public List<Vehicle> searchByCategory(VehicleCategory category) { return vehicleRepository.findByCategory(category); }
    public List<Vehicle> searchByStatus(VehicleStatus status) { return vehicleRepository.findByStatus(status); }
    public List<Vehicle> findAll() { return vehicleRepository.findAll(); }

    public void changeStatus(Vehicle vehicle, VehicleStatus newStatus) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle is required");
        vehicle.setStatus(newStatus);
        vehicleRepository.save(vehicle);
    }
}
