package com.rental.ui;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.rental.model.Customer;
import com.rental.model.Reservation;
import com.rental.model.Vehicle;
import com.rental.service.CustomerService;
import com.rental.service.ReservationService;
import com.rental.service.VehicleService;

public class ReservationPanel extends JPanel {

    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final VehicleService vehicleService;

    private final JTextField customerIdField = new JTextField(10);
    private final JTextField vehicleRegField = new JTextField(10);
    private final JTextField pickupField = new JTextField(10);
    private final JTextField returnField = new JTextField(10);

    private final JTextField availFromField = new JTextField(10);
    private final JTextField availToField = new JTextField(10);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Reservation ID", "Customer", "Vehicle", "Pickup", "Return", "Est. Charge", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public ReservationPanel(ReservationService reservationService, CustomerService customerService,
                             VehicleService vehicleService) {
        this.reservationService = reservationService;
        this.customerService = customerService;
        this.vehicleService = vehicleService;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildActionPanel(), BorderLayout.SOUTH);

        refreshTable();
        UiTheme.style(this);
        UiTheme.styleTable(table);
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Create Reservation (FR11, FR14) - dates as yyyy-MM-dd"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(form, gbc, row++, "Customer ID:", customerIdField);
        addRow(form, gbc, row++, "Vehicle Reg. No.:", vehicleRegField);
        addRow(form, gbc, row++, "Pickup date:", pickupField);
        addRow(form, gbc, row++, "Return date:", returnField);

        JButton createBtn = new JButton("Create Reservation");
        createBtn.addActionListener(e -> createReservation());
        gbc.gridx = 1;
        gbc.gridy = row++;
        form.add(createBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Check availability from:"), gbc);
        gbc.gridx = 1;
        form.add(availFromField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("to:"), gbc);
        gbc.gridx = 1;
        form.add(availToField, gbc);
        JButton availBtn = new JButton("Show Available Vehicles (FR12)");
        availBtn.addActionListener(e -> showAvailableVehicles());
        gbc.gridx = 2;
        form.add(availBtn, gbc);

        return form;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton confirmBtn = new JButton("Confirm Selected (FR13)");
        confirmBtn.addActionListener(e -> confirmSelected());

        JButton cancelBtn = new JButton("Cancel Selected (FR16)");
        cancelBtn.addActionListener(e -> cancelSelected());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());

        panel.add(confirmBtn);
        panel.add(cancelBtn);
        panel.add(refreshBtn);
        return panel;
    }

    private void createReservation() {
        try {
            Customer customer = customerService.searchCustomers(customerIdField.getText().trim())
                    .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No customer with that ID"));
            Vehicle vehicle = vehicleService.searchByRegistrationNumber(vehicleRegField.getText().trim())
                    .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No vehicle with that registration number"));

            LocalDate pickup = LocalDate.parse(pickupField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate ret = LocalDate.parse(returnField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);

            Reservation r = reservationService.createReservation(customer, vehicle, pickup, ret);
            JOptionPane.showMessageDialog(this, "Created reservation " + r.getReservationId()
                    + "\nEstimated charge: " + r.getEstimatedCharge());
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAvailableVehicles() {
        try {
            LocalDate from = LocalDate.parse(availFromField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate to = LocalDate.parse(availToField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            List<Vehicle> available = reservationService.findAvailableVehicles(from, to);
            StringBuilder sb = new StringBuilder();
            for (Vehicle v : available) sb.append(v.getRegistrationNumber()).append(" - ").append(v.getMake()).append(" ").append(v.getModel()).append("\n");
            if (sb.length() == 0) sb.append("No vehicles available for that period.");
            JOptionPane.showMessageDialog(this, sb.toString(), "Available Vehicles", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmSelected() {
        Reservation r = getSelectedReservation();
        if (r == null) return;
        try {
            reservationService.confirmReservation(r);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelected() {
        Reservation r = getSelectedReservation();
        if (r == null) return;
        try {
            reservationService.cancelReservation(r);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Reservation getSelectedReservation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a reservation first", "No selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        return reservationService.findAll().stream().filter(r -> r.getReservationId().equals(id)).findFirst().orElse(null);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Reservation r : reservationService.findAll()) {
            tableModel.addRow(new Object[]{
                    r.getReservationId(),
                    r.getCustomer() != null ? r.getCustomer().getCustomerId() : "-",
                    r.getVehicle() != null ? r.getVehicle().getRegistrationNumber() : "-",
                    r.getPickupDate(), r.getReturnDate(), r.getEstimatedCharge(), r.getStatus()
            });
        }
    }

    public void refresh() { refreshTable(); }
}
