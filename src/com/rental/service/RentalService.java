package com.rental.service;

import java.time.LocalDateTime;
import java.util.List;

import com.rental.enums.FuelLevel;
import com.rental.enums.ReservationStatus;
import com.rental.enums.VehicleStatus;
import com.rental.model.Charge;
import com.rental.model.Reservation;
import com.rental.model.RentalAgreement;
import com.rental.repository.RentalAgreementRepository;
import com.rental.repository.VehicleRepository;
import com.rental.service.charge.ChargeRule;
import com.rental.service.charge.DamageCharge;
import com.rental.service.charge.ExcessMileageCharge;
import com.rental.service.charge.FuelShortageCharge;
import com.rental.service.charge.LateReturnCharge;
import com.rental.util.IdGenerator;

public class RentalService {
    private final RentalAgreementRepository rentalAgreementRepository;
    private final VehicleRepository vehicleRepository;

    private final List<ChargeRule> chargeRules = List.of(
            new LateReturnCharge(), new ExcessMileageCharge(), new FuelShortageCharge(), new DamageCharge());

    public RentalService(RentalAgreementRepository rentalAgreementRepository, VehicleRepository vehicleRepository) {
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public RentalAgreement createAgreementFromReservation(Reservation reservation) {
        if (reservation == null) throw new IllegalArgumentException("Reservation is required");
        if (reservation.getStatus() != ReservationStatus.CONFIRMED)
            throw new IllegalStateException("Reservation must be CONFIRMED before creating a rental agreement");
        String id = IdGenerator.next("AGR");
        RentalAgreement agreement = new RentalAgreement(id, reservation);
        rentalAgreementRepository.save(agreement);
        return agreement;
    }

    public void handoverVehicle(RentalAgreement agreement, LocalDateTime pickupDateTime,
                                 double startMileage, FuelLevel startFuelLevel, String existingDamageNotes) {
        if (agreement == null) throw new IllegalArgumentException("Rental agreement is required");
        agreement.recordHandover(pickupDateTime, startMileage, startFuelLevel, existingDamageNotes);
        Reservation reservation = agreement.getReservation();
        reservation.setStatus(ReservationStatus.COLLECTED);
        reservation.getVehicle().setStatus(VehicleStatus.RENTED);
        reservation.getVehicle().setCurrentMileage(startMileage);
        rentalAgreementRepository.save(agreement);
        vehicleRepository.save(reservation.getVehicle());
        reservation.getCustomer().addRentalToHistory(agreement);
    }

    public void returnVehicle(RentalAgreement agreement, LocalDateTime returnDateTime,
                               double endMileage, FuelLevel endFuelLevel, String newDamageNotes,
                               boolean needsMaintenance) {
        if (agreement == null) throw new IllegalArgumentException("Rental agreement is required");
        agreement.recordReturn(returnDateTime, endMileage, endFuelLevel, newDamageNotes);

        for (ChargeRule rule : chargeRules) {
            Charge charge = rule.apply(agreement);
            if (charge.getAmount() > 0) agreement.addCharge(charge);
        }

        double base = agreement.getReservation().getEstimatedCharge();
        double finalAmount = base + sumCharges(agreement.getCharges());
        agreement.setFinalAmount(finalAmount);

        var vehicle = agreement.getReservation().getVehicle();
        vehicle.setCurrentMileage(endMileage);
        vehicle.setStatus(needsMaintenance ? VehicleStatus.UNDER_MAINTENANCE : VehicleStatus.AVAILABLE);

        rentalAgreementRepository.save(agreement);
        vehicleRepository.save(vehicle);
    }

    private double sumCharges(List<Charge> charges) {
        double sum = 0.0;
        for (Charge c : charges) sum += c.getAmount();
        return sum;
    }

    public List<RentalAgreement> findAll() { return rentalAgreementRepository.findAll(); }
}
