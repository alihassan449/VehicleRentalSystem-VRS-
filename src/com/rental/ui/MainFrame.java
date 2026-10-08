package com.rental.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.rental.report.ReportService;
import com.rental.service.CustomerService;
import com.rental.service.MaintenanceService;
import com.rental.service.PaymentService;
import com.rental.service.RentalService;
import com.rental.service.ReservationService;
import com.rental.service.VehicleService;

public class MainFrame extends JFrame {

    public MainFrame(CustomerService customerService,
                      VehicleService vehicleService,
                      ReservationService reservationService,
                      RentalService rentalService,
                      MaintenanceService maintenanceService,
                      PaymentService paymentService,
                      ReportService reportService) {
        setTitle("Vehicle Rental Management System");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildHeaderBanner(), BorderLayout.NORTH);
        add(buildTabs(customerService, vehicleService, reservationService, rentalService,
                maintenanceService, paymentService, reportService), BorderLayout.CENTER);
    }

    private JPanel buildHeaderBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(UiTheme.PRIMARY_DARK);
        banner.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Vehicle Rental Management System");
        title.setFont(UiTheme.HEADER_FONT);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Customers  \u00b7  Vehicles  \u00b7  Reservations  \u00b7  Rentals  \u00b7  Payments  \u00b7  Maintenance  \u00b7  Reports");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(0xCFE0F5));

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new javax.swing.BoxLayout(textStack, javax.swing.BoxLayout.Y_AXIS));
        textStack.add(title);
        textStack.add(subtitle);

        banner.add(textStack, BorderLayout.WEST);
        return banner;
    }

    private JTabbedPane buildTabs(CustomerService customerService,
                                   VehicleService vehicleService,
                                   ReservationService reservationService,
                                   RentalService rentalService,
                                   MaintenanceService maintenanceService,
                                   PaymentService paymentService,
                                   ReportService reportService) {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(UiTheme.BACKGROUND);

        tabs.addTab("Customers", new CustomerPanel(customerService));
        tabs.addTab("Vehicles", new VehiclePanel(vehicleService));
        tabs.addTab("Reservations", new ReservationPanel(reservationService, customerService, vehicleService));
        tabs.addTab("Rentals", new RentalPanel(rentalService, reservationService));
        tabs.addTab("Payments", new PaymentPanel(paymentService, rentalService));
        tabs.addTab("Maintenance", new MaintenancePanel(maintenanceService, vehicleService));
        tabs.addTab("Reports", new ReportPanel(reportService));

        return tabs;
    }
}
