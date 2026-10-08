package com.rental.service.charge;

import com.rental.enums.FuelLevel;
import com.rental.model.Charge;
import com.rental.model.RentalAgreement;

public class FuelShortageCharge implements ChargeRule {
    public static final double FEE_PER_MISSING_STEP = 15.00;

    @Override
    public Charge apply(RentalAgreement agreement) {
        FuelLevel start = agreement.getStartFuelLevel();
        FuelLevel end = agreement.getEndFuelLevel();
        if (start == null || end == null) return new Charge("Fuel shortage", 0.0);
        int missingSteps = start.ordinal() - end.ordinal();
        if (missingSteps <= 0) return new Charge("Fuel shortage", 0.0);
        double amount = missingSteps * FEE_PER_MISSING_STEP;
        return new Charge("Fuel shortage (" + missingSteps + " level(s) short)", amount);
    }
}
