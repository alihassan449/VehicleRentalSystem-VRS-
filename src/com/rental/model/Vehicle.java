package com.rental.model;

import java.util.ArrayList;
import java.util.List;

import com.rental.enums.FuelType;
import com.rental.enums.TransmissionType;
import com.rental.enums.VehicleCategory;
import com.rental.enums.VehicleStatus;

public class Vehicle {
    private String registrationNumber;
    private String make;
    private String model;
    private int year;
    private VehicleCategory category;
    private TransmissionType transmissionType;
    private FuelType fuelType;
    private double currentMileage;
    private double dailyRentalRate;
    private VehicleStatus status;
    private List<MaintenanceRecord> maintenanceHistory = new ArrayList<>();

    public Vehicle() {}

    public Vehicle(String registrationNumber, String make, String model, int year,
                    VehicleCategory category, TransmissionType transmissionType, FuelType fuelType,
                    double currentMileage, double dailyRentalRate) {
        this.registrationNumber = registrationNumber; this.make = make; this.model = model; this.year = year;
        this.category = category; this.transmissionType = transmissionType; this.fuelType = fuelType;
        this.currentMileage = currentMileage; this.dailyRentalRate = dailyRentalRate;
        this.status = VehicleStatus.AVAILABLE;
    }

    public void addMaintenanceRecord(MaintenanceRecord record) {
        if (record != null && !maintenanceHistory.contains(record)) maintenanceHistory.add(record);
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public VehicleCategory getCategory() { return category; }
    public void setCategory(VehicleCategory category) { this.category = category; }
    public TransmissionType getTransmissionType() { return transmissionType; }
    public void setTransmissionType(TransmissionType transmissionType) { this.transmissionType = transmissionType; }
    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
    public double getCurrentMileage() { return currentMileage; }
    public void setCurrentMileage(double currentMileage) { this.currentMileage = currentMileage; }
    public double getDailyRentalRate() { return dailyRentalRate; }
    public void setDailyRentalRate(double dailyRentalRate) { this.dailyRentalRate = dailyRentalRate; }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    public List<MaintenanceRecord> getMaintenanceHistory() { return maintenanceHistory; }

    @Override
    public String toString() { return registrationNumber + " - " + make + " " + model + " (" + year + ") [" + status + "]"; }
}
