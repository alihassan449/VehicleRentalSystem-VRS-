package com.rental.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.rental.enums.ReservationStatus;

public class Reservation {
    private String reservationId;
    private Customer customer;
    private Vehicle vehicle;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private ReservationStatus status;
    private double estimatedCharge;

    public Reservation() {}

    public Reservation(String reservationId, Customer customer, Vehicle vehicle,
                        LocalDate pickupDate, LocalDate returnDate) {
        this.reservationId = reservationId; this.customer = customer; this.vehicle = vehicle;
        this.pickupDate = pickupDate; this.returnDate = returnDate;
        this.status = ReservationStatus.PENDING;
        this.estimatedCharge = calculateEstimatedCharge();
    }

    public boolean overlapsWith(Reservation other) {
        if (other == null || other.vehicle == null || this.vehicle == null) return false;
        if (!this.vehicle.getRegistrationNumber().equals(other.vehicle.getRegistrationNumber())) return false;
        return !this.returnDate.isBefore(other.pickupDate) && !other.returnDate.isBefore(this.pickupDate);
    }

    public double calculateEstimatedCharge() {
        if (pickupDate == null || returnDate == null || vehicle == null) return 0.0;
        long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
        if (days < 1) days = 1;
        this.estimatedCharge = days * vehicle.getDailyRentalRate();
        return this.estimatedCharge;
    }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public double getEstimatedCharge() { return estimatedCharge; }
    public void setEstimatedCharge(double estimatedCharge) { this.estimatedCharge = estimatedCharge; }

    @Override
    public String toString() {
        return reservationId + " - " + (vehicle != null ? vehicle.getRegistrationNumber() : "?")
                + " [" + pickupDate + " to " + returnDate + "] " + status;
    }
}
