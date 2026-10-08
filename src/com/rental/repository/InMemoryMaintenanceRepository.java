package com.rental.repository;

import java.util.ArrayList;
import java.util.List;

import com.rental.model.MaintenanceRecord;
import com.rental.model.Vehicle;

public class InMemoryMaintenanceRepository implements MaintenanceRepository {
    private final List<MaintenanceRecord> records = new ArrayList<>();

    @Override
    public void save(MaintenanceRecord record) { records.add(record); }

    @Override
    public List<MaintenanceRecord> findByVehicle(Vehicle vehicle) {
        List<MaintenanceRecord> result = new ArrayList<>();
        if (vehicle == null) return result;
        for (MaintenanceRecord r : records)
            if (r.getVehicle() != null && r.getVehicle().getRegistrationNumber().equals(vehicle.getRegistrationNumber()))
                result.add(r);
        return result;
    }

    @Override
    public List<MaintenanceRecord> findAll() { return new ArrayList<>(records); }
}
