package com.rental.service;

import java.time.LocalDate;
import java.util.List;

import com.rental.enums.VehicleStatus;
import com.rental.model.MaintenanceRecord;
import com.rental.model.Vehicle;
import com.rental.repository.MaintenanceRepository;
import com.rental.repository.VehicleRepository;
import com.rental.util.IdGenerator;

public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, VehicleRepository vehicleRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public MaintenanceRecord createMaintenanceRecord(Vehicle vehicle, String type, String description,
                                                       double cost, boolean completed) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle is required");
        String id = IdGenerator.next("MNT");
        MaintenanceRecord record = new MaintenanceRecord(id, vehicle, type, LocalDate.now(), description, cost, completed);
        maintenanceRepository.save(record);
        vehicle.addMaintenanceRecord(record);
        if (!completed) {
            vehicle.setStatus(VehicleStatus.UNDER_MAINTENANCE);
        } else if (vehicle.getStatus() == VehicleStatus.UNDER_MAINTENANCE) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }
        vehicleRepository.save(vehicle);
        return record;
    }

    public boolean isUnderMaintenance(Vehicle vehicle) {
        return vehicle != null && vehicle.getStatus() == VehicleStatus.UNDER_MAINTENANCE;
    }

    public void completeMaintenance(MaintenanceRecord record) {
        if (record == null) throw new IllegalArgumentException("Maintenance record is required");
        record.setCompleted(true);
        Vehicle vehicle = record.getVehicle();
        if (vehicle != null && vehicle.getStatus() == VehicleStatus.UNDER_MAINTENANCE) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }
    }

    public List<MaintenanceRecord> findAll() { return maintenanceRepository.findAll(); }
}
