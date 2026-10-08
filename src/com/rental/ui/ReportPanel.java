package com.rental.ui;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

import com.rental.enums.VehicleStatus;
import com.rental.model.MaintenanceRecord;
import com.rental.model.RentalAgreement;
import com.rental.model.Vehicle;
import com.rental.report.ReportService;

public class ReportPanel extends JPanel {

    private final ReportService reportService;

    private final JComboBox<VehicleStatus> statusBox = new JComboBox<>(VehicleStatus.values());
    private final JTextField customerIdField = new JTextField(10);
    private final JTextField vehicleRegField = new JTextField(10);
    private final JTextField revenueFromField = new JTextField(10);
    private final JTextField revenueToField = new JTextField(10);

    private final JTextArea outputArea = new JTextArea(20, 60);

    public ReportPanel(ReportService reportService) {
        this.reportService = reportService;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildControlsPanel(), BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setBackground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        UiTheme.style(this);
    }

    private JPanel buildControlsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setBorder(BorderFactory.createTitledBorder("Fleet status report (FR28)"));
        JButton statusBtn = new JButton("Show Vehicles");
        statusBtn.addActionListener(e -> showVehiclesByStatus());
        row1.add(new JLabel("Status:"));
        row1.add(statusBox);
        row1.add(statusBtn);
        panel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBorder(BorderFactory.createTitledBorder("Customer rental history (FR29)"));
        JButton custBtn = new JButton("Show History");
        custBtn.addActionListener(e -> showCustomerHistory());
        row2.add(new JLabel("Customer ID:"));
        row2.add(customerIdField);
        row2.add(custBtn);
        panel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.setBorder(BorderFactory.createTitledBorder("Vehicle rental & maintenance history (FR30)"));
        JButton vehBtn = new JButton("Show History");
        vehBtn.addActionListener(e -> showVehicleHistory());
        row3.add(new JLabel("Vehicle Reg. No.:"));
        row3.add(vehicleRegField);
        row3.add(vehBtn);
        panel.add(row3);

        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row4.setBorder(BorderFactory.createTitledBorder("Revenue report (FR31) - dates as yyyy-MM-dd"));
        JButton revBtn = new JButton("Show Revenue");
        revBtn.addActionListener(e -> showRevenue());
        row4.add(new JLabel("From:"));
        row4.add(revenueFromField);
        row4.add(new JLabel("To:"));
        row4.add(revenueToField);
        row4.add(revBtn);
        panel.add(row4);

        return panel;
    }

    private void showVehiclesByStatus() {
        VehicleStatus status = (VehicleStatus) statusBox.getSelectedItem();
        StringBuilder sb = new StringBuilder("Vehicles with status " + status + ":\n\n");
        for (Vehicle v : reportService.vehiclesByStatus(status)) sb.append(v).append("\n");
        outputArea.setText(sb.toString());
    }

    private void showCustomerHistory() {
        String customerId = customerIdField.getText().trim();
        StringBuilder sb = new StringBuilder("Rental history for customer " + customerId + ":\n\n");
        for (RentalAgreement a : reportService.customerRentalHistory(customerId))
            sb.append(a).append(" - Final amount: ").append(a.getFinalAmount()).append("\n");
        outputArea.setText(sb.toString());
    }

    private void showVehicleHistory() {
        try {
            String reg = vehicleRegField.getText().trim();
            Vehicle vehicle = null;
            for (VehicleStatus s : VehicleStatus.values()) {
                vehicle = reportService.vehiclesByStatus(s).stream()
                        .filter(v -> v.getRegistrationNumber().equals(reg))
                        .findFirst().orElse(vehicle);
            }
            if (vehicle == null) {
                outputArea.setText("No vehicle found with registration number " + reg);
                return;
            }
            StringBuilder sb = new StringBuilder("Rental history for vehicle " + reg + ":\n\n");
            for (RentalAgreement a : reportService.vehicleRentalHistory(vehicle))
                sb.append(a).append(" - Final amount: ").append(a.getFinalAmount()).append("\n");
            sb.append("\nMaintenance history for vehicle ").append(reg).append(":\n\n");
            for (MaintenanceRecord m : reportService.vehicleMaintenanceHistory(vehicle)) sb.append(m).append("\n");
            outputArea.setText(sb.toString());
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void showRevenue() {
        try {
            LocalDate from = LocalDate.parse(revenueFromField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate to = LocalDate.parse(revenueToField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            double revenue = reportService.totalRevenue(from, to);
            outputArea.setText(String.format("Total revenue from %s to %s: %.2f", from, to, revenue));
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage() + "\nDates must be in yyyy-MM-dd format.");
        }
    }

    public void refresh() {
    }
}
