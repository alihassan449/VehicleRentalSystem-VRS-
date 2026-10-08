package com.rental.report;

import com.rental.model.Charge;
import com.rental.model.RentalAgreement;

public class Invoice {
    private final RentalAgreement agreement;

    public Invoice(RentalAgreement agreement) { this.agreement = agreement; }

    public String render() {
        StringBuilder sb = new StringBuilder();
        var reservation = agreement.getReservation();
        var vehicle = reservation.getVehicle();
        var customer = reservation.getCustomer();

        sb.append("================ RENTAL INVOICE ================\n");
        sb.append("Agreement ID   : ").append(agreement.getAgreementId()).append("\n");
        sb.append("Customer       : ").append(customer.getName()).append(" (").append(customer.getCustomerId()).append(")\n");
        sb.append("Vehicle        : ").append(vehicle.getMake()).append(" ").append(vehicle.getModel())
                .append(" [").append(vehicle.getRegistrationNumber()).append("]\n");
        sb.append("Pickup         : ").append(agreement.getPickupDateTime()).append("\n");
        sb.append("Return         : ").append(agreement.getReturnDateTime()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("Base charge    : %.2f%n", reservation.getEstimatedCharge()));
        for (Charge c : agreement.getCharges()) sb.append(String.format("%-15s: %.2f%n", c.getDescription(), c.getAmount()));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("TOTAL DUE      : %.2f%n", agreement.getFinalAmount()));
        sb.append(String.format("PAID SO FAR    : %.2f%n", agreement.totalPaid()));
        sb.append(String.format("BALANCE        : %.2f%n", agreement.balanceDue()));
        sb.append("==================================================\n");
        return sb.toString();
    }
}
