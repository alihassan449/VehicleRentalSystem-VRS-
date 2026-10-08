package com.rental.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.rental.enums.ReservationStatus;
import com.rental.enums.VehicleStatus;
import com.rental.model.Customer;
import com.rental.model.Reservation;
import com.rental.model.Vehicle;
import com.rental.repository.ReservationRepository;
import com.rental.repository.VehicleRepository;
import com.rental.util.IdGenerator;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerService customerService;

    public ReservationService(ReservationRepository reservationRepository, VehicleRepository vehicleRepository,
                               CustomerService customerService) {
        this.reservationRepository = reservationRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerService = customerService;
    }

    public Reservation createReservation(Customer customer, Vehicle vehicle, LocalDate pickupDate, LocalDate returnDate) {
        if (customer == null || vehicle == null || pickupDate == null || returnDate == null)
            throw new IllegalArgumentException("Customer, vehicle, pickup date and return date are all required");
        if (returnDate.isBefore(pickupDate)) throw new IllegalArgumentException("Return date cannot be before pickup date");
        if (!customerService.isEligibleForRental(customer)) throw new IllegalStateException("Customer's driving license is expired");
        if (vehicle.getStatus() == VehicleStatus.UNDER_MAINTENANCE || vehicle.getStatus() == VehicleStatus.INACTIVE)
            throw new IllegalStateException("Vehicle is not available for reservation (status: " + vehicle.getStatus() + ")");

        String id = IdGenerator.next("RES");
        Reservation reservation = new Reservation(id, customer, vehicle, pickupDate, returnDate);
        reservationRepository.save(reservation);
        return reservation;
    }

    public List<Vehicle> findAvailableVehicles(LocalDate pickupDate, LocalDate returnDate) {
        List<Vehicle> available = new ArrayList<>();
        for (Vehicle vehicle : vehicleRepository.findAll()) {
            if (vehicle.getStatus() != VehicleStatus.AVAILABLE) continue;
            Reservation candidate = new Reservation(null, null, vehicle, pickupDate, returnDate);
            boolean overlapsConfirmed = false;
            for (Reservation existing : reservationRepository.findByVehicle(vehicle)) {
                if (existing.getStatus() == ReservationStatus.CONFIRMED && candidate.overlapsWith(existing)) {
                    overlapsConfirmed = true;
                    break;
                }
            }
            if (!overlapsConfirmed) available.add(vehicle);
        }
        return available;
    }

    public boolean hasOverlappingConfirmedReservation(Reservation candidate) {
        if (candidate == null || candidate.getVehicle() == null) return false;
        for (Reservation existing : reservationRepository.findByVehicle(candidate.getVehicle())) {
            if (existing == candidate) continue;
            if (existing.getStatus() == ReservationStatus.CONFIRMED && candidate.overlapsWith(existing)) return true;
        }
        return false;
    }

    public void confirmReservation(Reservation reservation) {
        if (reservation == null) throw new IllegalArgumentException("Reservation is required");
        if (hasOverlappingConfirmedReservation(reservation))
            throw new IllegalStateException("Vehicle already has a confirmed reservation that overlaps this period");
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.getVehicle().setStatus(VehicleStatus.RESERVED);
        reservationRepository.save(reservation);
        vehicleRepository.save(reservation.getVehicle());
    }

    public void cancelReservation(Reservation reservation) {
        if (reservation == null) throw new IllegalArgumentException("Reservation is required");
        if (reservation.getStatus() == ReservationStatus.COLLECTED)
            throw new IllegalStateException("Cannot cancel a reservation after the vehicle has been collected");
        reservation.setStatus(ReservationStatus.CANCELLED);
        if (reservation.getVehicle().getStatus() == VehicleStatus.RESERVED) {
            reservation.getVehicle().setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(reservation.getVehicle());
        }
        reservationRepository.save(reservation);
    }

    public List<Reservation> findAll() { return reservationRepository.findAll(); }
    public List<Reservation> findByCustomer(String customerId) { return reservationRepository.findByCustomerId(customerId); }
}
