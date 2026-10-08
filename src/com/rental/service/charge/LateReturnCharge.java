package com.rental.service.charge;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.rental.model.Charge;
import com.rental.model.RentalAgreement;

public class LateReturnCharge implements ChargeRule {
    public static final double LATE_FEE_PER_DAY = 20.00;

    @Override
    public Charge apply(RentalAgreement agreement) {
        if (agreement.getReturnDateTime() == null || agreement.getReservation() == null
                || agreement.getReservation().getReturnDate() == null) return new Charge("Late return", 0.0);
        LocalDate plannedReturn = agreement.getReservation().getReturnDate();
        LocalDate actualReturn = agreement.getReturnDateTime().toLocalDate();
        long lateDays = ChronoUnit.DAYS.between(plannedReturn, actualReturn);
        if (lateDays <= 0) return new Charge("Late return", 0.0);
        double amount = lateDays * LATE_FEE_PER_DAY;
        return new Charge("Late return (" + lateDays + " day(s) late)", amount);
    }
}
