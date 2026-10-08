package com.rental.service.charge;

import com.rental.model.Charge;
import com.rental.model.RentalAgreement;

public interface ChargeRule {
    Charge apply(RentalAgreement agreement);
}
