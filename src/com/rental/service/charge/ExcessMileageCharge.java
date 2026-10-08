package com.rental.service.charge;

import java.time.temporal.ChronoUnit;

import com.rental.model.Charge;
import com.rental.model.RentalAgreement;

public class ExcessMileageCharge implements ChargeRule {
    public static final double ALLOWED_KM_PER_DAY = 200.0;
    public static final double RATE_PER_EXCESS_KM = 0.50;

    @Override
    public Charge apply(RentalAgreement agreement) {
        if (agreement.getReservation() == null) return new Charge("Excess mileage", 0.0);
        double distanceDriven = agreement.getEndMileage() - agreement.getStartMileage();
        if (distanceDriven <= 0) return new Charge("Excess mileage", 0.0);
        long rentalDays = ChronoUnit.DAYS.between(
                agreement.getReservation().getPickupDate(), agreement.getReservation().getReturnDate());
        if (rentalDays < 1) rentalDays = 1;
        double allowedDistance = rentalDays * ALLOWED_KM_PER_DAY;
        double excess = distanceDriven - allowedDistance;
        if (excess <= 0) return new Charge("Excess mileage", 0.0);
        double amount = excess * RATE_PER_EXCESS_KM;
        return new Charge(String.format("Excess mileage (%.0f km over)", excess), amount);
    }
}
