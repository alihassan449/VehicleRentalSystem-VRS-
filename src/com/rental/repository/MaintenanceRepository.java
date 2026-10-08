package com.rental.repository;

import java.util.List;

import com.rental.model.MaintenanceRecord;
import com.rental.model.Vehicle;

public interface MaintenanceRepository {
    void save(MaintenanceRecord record);
    List<MaintenanceRecord> findByVehicle(Vehicle vehicle);
    List<MaintenanceRecord> findAll();
}
