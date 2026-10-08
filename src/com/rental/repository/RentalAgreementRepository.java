package com.rental.repository;

import java.util.List;
import java.util.Optional;

import com.rental.model.RentalAgreement;
import com.rental.model.Vehicle;

public interface RentalAgreementRepository {
    void save(RentalAgreement agreement);
    Optional<RentalAgreement> findById(String agreementId);
    List<RentalAgreement> findByVehicle(Vehicle vehicle);
    List<RentalAgreement> findByCustomerId(String customerId);
    List<RentalAgreement> findAll();
}
