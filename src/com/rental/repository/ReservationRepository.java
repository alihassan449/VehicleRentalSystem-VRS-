package com.rental.repository;

import java.util.List;
import java.util.Optional;

import com.rental.model.Reservation;
import com.rental.model.Vehicle;

public interface ReservationRepository {
    void save(Reservation reservation);
    Optional<Reservation> findById(String reservationId);
    List<Reservation> findByVehicle(Vehicle vehicle);
    List<Reservation> findByCustomerId(String customerId);
    List<Reservation> findAll();
}
