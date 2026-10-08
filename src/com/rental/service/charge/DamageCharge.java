package com.rental.service.charge;

import com.rental.model.Charge;
import com.rental.model.RentalAgreement;

public class DamageCharge implements ChargeRule {
    public static final double FEE_PER_DAMAGE_ENTRY = 50.00;

    @Override
    public Charge apply(RentalAgreement agreement) {
        String notes = agreement.getNewDamageNotes();
        if (notes == null || notes.isBlank()) return new Charge("Damage", 0.0);
        String[] entries = notes.split(";");
        int count = 0;
        for (String entry : entries) if (!entry.isBlank()) count++;
        if (count == 0) return new Charge("Damage", 0.0);
        double amount = count * FEE_PER_DAMAGE_ENTRY;
        return new Charge("Damage (" + count + " item(s))", amount);
    }
}
