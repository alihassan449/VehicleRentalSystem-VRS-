package com.rental.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.rental.model.Reservation;
import com.rental.model.Vehicle;

public class InMemoryReservationRepository implements ReservationRepository {
    private final Map<String, Reservation> reservationsById = new HashMap<>();

    @Override
    public void save(Reservation reservation) { reservationsById.put(reservation.getReservationId(), reservation); }

    @Override
    public Optional<Reservation> findById(String reservationId) {
        return Optional.ofNullable(reservationsById.get(reservationId));
    }

    @Override
    public List<Reservation> findByVehicle(Vehicle vehicle) {
        List<Reservation> result = new ArrayList<>();
        if (vehicle == null) return result;
        for (Reservation r : reservationsById.values())
            if (r.getVehicle() != null && r.getVehicle().getRegistrationNumber().equals(vehicle.getRegistrationNumber()))
                result.add(r);
        return result;
    }

    @Override
    public List<Reservation> findByCustomerId(String customerId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservationsById.values())
            if (r.getCustomer() != null && r.getCustomer().getCustomerId().equals(customerId)) result.add(r);
        return result;
    }

    @Override
    public List<Reservation> findAll() { return new ArrayList<>(reservationsById.values()); }
}
