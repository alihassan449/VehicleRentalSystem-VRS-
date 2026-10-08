package com.rental.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.rental.enums.FuelLevel;

public class RentalAgreement {
    private String agreementId;
    private Reservation reservation;
    private LocalDateTime pickupDateTime;
    private double startMileage;
    private FuelLevel startFuelLevel;
    private String existingDamageNotes;
    private LocalDateTime returnDateTime;
    private double endMileage;
    private FuelLevel endFuelLevel;
    private String newDamageNotes;
    private boolean returned = false;
    private List<Charge> charges = new ArrayList<>();
    private double finalAmount;
    private List<Payment> payments = new ArrayList<>();

    public RentalAgreement() {}
    public RentalAgreement(String agreementId, Reservation reservation) {
        this.agreementId = agreementId; this.reservation = reservation;
    }

    public void recordHandover(LocalDateTime pickupDateTime, double startMileage,
                                FuelLevel startFuelLevel, String existingDamageNotes) {
        this.pickupDateTime = pickupDateTime; this.startMileage = startMileage;
        this.startFuelLevel = startFuelLevel; this.existingDamageNotes = existingDamageNotes;
    }

    public void recordReturn(LocalDateTime returnDateTime, double endMileage,
                              FuelLevel endFuelLevel, String newDamageNotes) {
        this.returnDateTime = returnDateTime; this.endMileage = endMileage;
        this.endFuelLevel = endFuelLevel; this.newDamageNotes = newDamageNotes;
        this.returned = true;
    }

    public void addCharge(Charge charge) { if (charge != null) charges.add(charge); }
    public void addPayment(Payment payment) { if (payment != null) payments.add(payment); }

    public double totalPaid() {
        double sum = 0.0;
        for (Payment p : payments) sum += p.getAmountPaid();
        return sum;
    }

    public double balanceDue() { return finalAmount - totalPaid(); }

    public String getAgreementId() { return agreementId; }
    public void setAgreementId(String agreementId) { this.agreementId = agreementId; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public List<Charge> getCharges() { return charges; }
    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
    public double getStartMileage() { return startMileage; }
    public double getEndMileage() { return endMileage; }
    public LocalDateTime getPickupDateTime() { return pickupDateTime; }
    public LocalDateTime getReturnDateTime() { return returnDateTime; }
    public FuelLevel getStartFuelLevel() { return startFuelLevel; }
    public FuelLevel getEndFuelLevel() { return endFuelLevel; }
    public String getExistingDamageNotes() { return existingDamageNotes; }
    public String getNewDamageNotes() { return newDamageNotes; }
    public List<Payment> getPayments() { return payments; }
    public boolean isReturned() { return returned; }

    @Override
    public String toString() {
        String reg = reservation != null && reservation.getVehicle() != null
                ? reservation.getVehicle().getRegistrationNumber() : "?";
        return agreementId + " - " + reg + " [" + (returned ? "Returned" : "Active") + "]";
    }
}
