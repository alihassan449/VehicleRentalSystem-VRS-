package com.rental;

import javax.swing.SwingUtilities;

import com.rental.repository.InMemoryCustomerRepository;
import com.rental.repository.InMemoryMaintenanceRepository;
import com.rental.repository.InMemoryPaymentRepository;
import com.rental.repository.InMemoryRentalAgreementRepository;
import com.rental.repository.InMemoryReservationRepository;
import com.rental.repository.InMemoryVehicleRepository;
import com.rental.report.ReportService;
import com.rental.service.CustomerService;
import com.rental.service.MaintenanceService;
import com.rental.service.PaymentService;
import com.rental.service.RentalService;
import com.rental.service.ReservationService;
import com.rental.service.VehicleService;
import com.rental.ui.MainFrame;
import com.rental.ui.UiTheme;

public class Main {

    public static void main(String[] args) {
        var customerRepository = new InMemoryCustomerRepository();
        var vehicleRepository = new InMemoryVehicleRepository();
        var reservationRepository = new InMemoryReservationRepository();
        var rentalAgreementRepository = new InMemoryRentalAgreementRepository();
        var maintenanceRepository = new InMemoryMaintenanceRepository();
        var paymentRepository = new InMemoryPaymentRepository();

        var customerService = new CustomerService(customerRepository);
        var vehicleService = new VehicleService(vehicleRepository);
        var reservationService = new ReservationService(reservationRepository, vehicleRepository, customerService);
        var rentalService = new RentalService(rentalAgreementRepository, vehicleRepository);
        var maintenanceService = new MaintenanceService(maintenanceRepository, vehicleRepository);
        var paymentService = new PaymentService(paymentRepository);
        var reportService = new ReportService(vehicleRepository, rentalAgreementRepository, paymentRepository);

        SwingUtilities.invokeLater(() -> {
            UiTheme.applyLookAndFeel();
            MainFrame frame = new MainFrame(customerService, vehicleService, reservationService,
                    rentalService, maintenanceService, paymentService, reportService);
            frame.setVisible(true);
        });
    }
}
