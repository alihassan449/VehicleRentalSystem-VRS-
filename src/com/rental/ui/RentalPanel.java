package com.rental.ui;

import java.awt.*;
import java.time.LocalDateTime;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.rental.enums.FuelLevel;
import com.rental.model.Reservation;
import com.rental.model.RentalAgreement;
import com.rental.report.Invoice;
import com.rental.service.RentalService;
import com.rental.service.ReservationService;

public class RentalPanel extends JPanel {

    private final RentalService rentalService;
    private final ReservationService reservationService;

    private final JTextField reservationIdField = new JTextField(10);

    private final JTextField startMileageField = new JTextField(8);
    private final JComboBox<FuelLevel> startFuelBox = new JComboBox<>(FuelLevel.values());
    private final JTextField existingDamageField = new JTextField(15);

    private final JTextField agreementIdField = new JTextField(10);
    private final JTextField endMileageField = new JTextField(8);
    private final JComboBox<FuelLevel> endFuelBox = new JComboBox<>(FuelLevel.values());
    private final JTextField newDamageField = new JTextField(15);
    private final JCheckBox needsMaintenanceBox = new JCheckBox("Needs maintenance after return");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Agreement ID", "Reservation", "Vehicle", "Pickup", "Return", "Final Amount", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public RentalPanel(RentalService rentalService, ReservationService reservationService) {
        this.rentalService = rentalService;
        this.reservationService = reservationService;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel forms = new JPanel(new GridLayout(1, 2, 10, 0));
        forms.add(buildCreateAndHandoverPanel());
        forms.add(buildReturnPanel());

        add(forms, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh / View Invoice for Selected");
        refreshBtn.addActionListener(e -> showInvoiceForSelected());
        add(refreshBtn, BorderLayout.SOUTH);

        refreshTable();
        UiTheme.style(this);
        UiTheme.styleTable(table);
    }

    private JPanel buildCreateAndHandoverPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Create Agreement + Handover (FR17-FR19)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(panel, gbc, row++, "Reservation ID:", reservationIdField);

        JButton createBtn = new JButton("Create Agreement from Reservation");
        createBtn.addActionListener(e -> createAgreement());
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(createBtn, gbc);

        addRow(panel, gbc, row++, "Start mileage:", startMileageField);
        addRow(panel, gbc, row++, "Start fuel level:", startFuelBox);
        addRow(panel, gbc, row++, "Existing damage notes:", existingDamageField);

        JButton handoverBtn = new JButton("Record Handover (uses Reservation ID above)");
        handoverBtn.addActionListener(e -> recordHandover());
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(handoverBtn, gbc);

        return panel;
    }

    private JPanel buildReturnPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Record Return (FR20-FR23)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(panel, gbc, row++, "Agreement ID:", agreementIdField);
        addRow(panel, gbc, row++, "End mileage:", endMileageField);
        addRow(panel, gbc, row++, "End fuel level:", endFuelBox);
        addRow(panel, gbc, row++, "New damage notes (; separated):", newDamageField);

        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(needsMaintenanceBox, gbc);

        JButton returnBtn = new JButton("Record Return & Calculate Charges");
        returnBtn.addActionListener(e -> recordReturn());
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(returnBtn, gbc);

        return panel;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private void createAgreement() {
        try {
            Reservation r = findReservation(reservationIdField.getText().trim());
            RentalAgreement agreement = rentalService.createAgreementFromReservation(r);
            agreementIdField.setText(agreement.getAgreementId());
            JOptionPane.showMessageDialog(this, "Created agreement " + agreement.getAgreementId());
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recordHandover() {
        try {
            RentalAgreement agreement = findAgreementByReservation(reservationIdField.getText().trim());
            double startMileage = Double.parseDouble(startMileageField.getText().trim());
            FuelLevel startFuel = (FuelLevel) startFuelBox.getSelectedItem();
            rentalService.handoverVehicle(agreement, LocalDateTime.now(), startMileage, startFuel, existingDamageField.getText().trim());
            JOptionPane.showMessageDialog(this, "Handover recorded. Vehicle is now RENTED (FR19).");
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Start mileage must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recordReturn() {
        try {
            RentalAgreement agreement = findAgreement(agreementIdField.getText().trim());
            double endMileage = Double.parseDouble(endMileageField.getText().trim());
            FuelLevel endFuel = (FuelLevel) endFuelBox.getSelectedItem();
            rentalService.returnVehicle(agreement, LocalDateTime.now(), endMileage, endFuel,
                    newDamageField.getText().trim(), needsMaintenanceBox.isSelected());
            JOptionPane.showMessageDialog(this, new Invoice(agreement).render(), "Return Recorded - Invoice", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "End mileage must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInvoiceForSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { refreshTable(); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        RentalAgreement agreement = findAgreement(id);
        JOptionPane.showMessageDialog(this, new Invoice(agreement).render(), "Invoice", JOptionPane.INFORMATION_MESSAGE);
    }

    private Reservation findReservation(String id) {
        return reservationService.findAll().stream().filter(r -> r.getReservationId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No reservation with ID " + id));
    }

    private RentalAgreement findAgreement(String id) {
        return rentalService.findAll().stream().filter(a -> a.getAgreementId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No rental agreement with ID " + id));
    }

    private RentalAgreement findAgreementByReservation(String reservationId) {
        return rentalService.findAll().stream().filter(a -> a.getReservation().getReservationId().equals(reservationId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No rental agreement found for reservation " + reservationId + " - create one first"));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (RentalAgreement a : rentalService.findAll()) {
            tableModel.addRow(new Object[]{
                    a.getAgreementId(),
                    a.getReservation() != null ? a.getReservation().getReservationId() : "-",
                    a.getReservation() != null && a.getReservation().getVehicle() != null
                            ? a.getReservation().getVehicle().getRegistrationNumber() : "-",
                    a.getPickupDateTime(), a.getReturnDateTime(), a.getFinalAmount(),
                    a.isReturned() ? "Returned" : "Active"
            });
        }
    }

    public void refresh() { refreshTable(); }
}
