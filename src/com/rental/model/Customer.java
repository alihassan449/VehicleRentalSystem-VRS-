package com.rental.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerId;
    private String name;
    private Address address;
    private String contactNumber;
    private String nationalId;
    private DrivingLicense drivingLicense;
    private List<RentalAgreement> rentalHistory = new ArrayList<>();

    public Customer() {}

    public Customer(String customerId, String name, Address address, String contactNumber,
                     String nationalId, DrivingLicense drivingLicense) {
        this.customerId = customerId; this.name = name; this.address = address;
        this.contactNumber = contactNumber; this.nationalId = nationalId; this.drivingLicense = drivingLicense;
    }

    public void updateContactInfo(String newContactNumber, Address newAddress) {
        if (newContactNumber != null && !newContactNumber.isBlank()) this.contactNumber = newContactNumber;
        if (newAddress != null) this.address = newAddress;
    }

    public void addRentalToHistory(RentalAgreement agreement) {
        if (agreement != null && !rentalHistory.contains(agreement)) rentalHistory.add(agreement);
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public DrivingLicense getDrivingLicense() { return drivingLicense; }
    public void setDrivingLicense(DrivingLicense drivingLicense) { this.drivingLicense = drivingLicense; }
    public List<RentalAgreement> getRentalHistory() { return rentalHistory; }

    @Override
    public String toString() { return customerId + " - " + name; }
}
