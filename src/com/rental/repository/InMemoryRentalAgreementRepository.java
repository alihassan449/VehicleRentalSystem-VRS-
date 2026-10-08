package com.rental.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.rental.model.RentalAgreement;
import com.rental.model.Vehicle;

public class InMemoryRentalAgreementRepository implements RentalAgreementRepository {
    private final Map<String, RentalAgreement> agreementsById = new HashMap<>();

    @Override
    public void save(RentalAgreement agreement) { agreementsById.put(agreement.getAgreementId(), agreement); }

    @Override
    public Optional<RentalAgreement> findById(String agreementId) {
        return Optional.ofNullable(agreementsById.get(agreementId));
    }

    @Override
    public List<RentalAgreement> findByVehicle(Vehicle vehicle) {
        List<RentalAgreement> result = new ArrayList<>();
        if (vehicle == null) return result;
        for (RentalAgreement a : agreementsById.values())
            if (a.getReservation() != null && a.getReservation().getVehicle() != null
                    && a.getReservation().getVehicle().getRegistrationNumber().equals(vehicle.getRegistrationNumber()))
                result.add(a);
        return result;
    }

    @Override
    public List<RentalAgreement> findByCustomerId(String customerId) {
        List<RentalAgreement> result = new ArrayList<>();
        for (RentalAgreement a : agreementsById.values())
            if (a.getReservation() != null && a.getReservation().getCustomer() != null
                    && a.getReservation().getCustomer().getCustomerId().equals(customerId))
                result.add(a);
        return result;
    }

    @Override
    public List<RentalAgreement> findAll() { return new ArrayList<>(agreementsById.values()); }
}
