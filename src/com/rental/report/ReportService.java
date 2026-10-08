package com.rental.report;

import java.time.LocalDate;
import java.util.List;

import com.rental.enums.VehicleStatus;
import com.rental.model.MaintenanceRecord;
import com.rental.model.Payment;
import com.rental.model.RentalAgreement;
import com.rental.model.Vehicle;
import com.rental.repository.PaymentRepository;
import com.rental.repository.RentalAgreementRepository;
import com.rental.repository.VehicleRepository;

public class ReportService {
    private final VehicleRepository vehicleRepository;
    private final RentalAgreementRepository rentalAgreementRepository;
    private final PaymentRepository paymentRepository;

    public ReportService(VehicleRepository vehicleRepository, RentalAgreementRepository rentalAgreementRepository,
                          PaymentRepository paymentRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<Vehicle> vehiclesByStatus(VehicleStatus status) { return vehicleRepository.findByStatus(status); }
    public List<RentalAgreement> customerRentalHistory(String customerId) { return rentalAgreementRepository.findByCustomerId(customerId); }
    public List<RentalAgreement> vehicleRentalHistory(Vehicle vehicle) { return rentalAgreementRepository.findByVehicle(vehicle); }
    public List<MaintenanceRecord> vehicleMaintenanceHistory(Vehicle vehicle) {
        return vehicle == null ? List.of() : vehicle.getMaintenanceHistory();
    }

    public double totalRevenue(LocalDate from, LocalDate to) {
        double sum = 0.0;
        for (Payment p : paymentRepository.findByDateRange(from, to)) sum += p.getAmountPaid();
        return sum;
    }
}
