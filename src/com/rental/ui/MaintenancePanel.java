package com.rental.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.rental.model.MaintenanceRecord;
import com.rental.model.Vehicle;
import com.rental.service.MaintenanceService;
import com.rental.service.VehicleService;

public class MaintenancePanel extends JPanel {

    private final MaintenanceService maintenanceService;
    private final VehicleService vehicleService;

    private final JTextField vehicleRegField = new JTextField(10);
    private final JTextField typeField = new JTextField(12);
    private final JTextField descriptionField = new JTextField(18);
    private final JTextField costField = new JTextField(8);
    private final JCheckBox completedBox = new JCheckBox("Completed");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Record ID", "Vehicle", "Type", "Date", "Description", "Cost", "Completed?"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public MaintenancePanel(MaintenanceService maintenanceService, VehicleService vehicleService) {
        this.maintenanceService = maintenanceService;
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
        form.setBorder(BorderFactory.createTitledBorder("Create Maintenance Record (FR26)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(form, gbc, row++, "Vehicle Reg. No.:", vehicleRegField);
        addRow(form, gbc, row++, "Maintenance type:", typeField);
        addRow(form, gbc, row++, "Description:", descriptionField);
        addRow(form, gbc, row++, "Cost:", costField);

        gbc.gridx = 1;
        gbc.gridy = row++;
        form.add(completedBox, gbc);

        JButton createBtn = new JButton("Create Record (blocks reservation/rental if not completed - FR27)");
        createBtn.addActionListener(e -> createRecord());
        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(createBtn, gbc);

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
        JButton completeBtn = new JButton("Mark Selected Completed");
        completeBtn.addActionListener(e -> completeSelected());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        panel.add(completeBtn);
        panel.add(refreshBtn);
        return panel;
    }

    private void createRecord() {
        try {
            Vehicle vehicle = vehicleService.searchByRegistrationNumber(vehicleRegField.getText().trim())
                    .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No vehicle with that registration number"));
            double cost = Double.parseDouble(costField.getText().trim());

            MaintenanceRecord record = maintenanceService.createMaintenanceRecord(
                    vehicle, typeField.getText().trim(), descriptionField.getText().trim(), cost, completedBox.isSelected());

            JOptionPane.showMessageDialog(this, "Created maintenance record " + record.getRecordId());
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cost must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a record first", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        MaintenanceRecord record = maintenanceService.findAll().stream().filter(r -> r.getRecordId().equals(id)).findFirst().orElse(null);
        if (record != null) {
            maintenanceService.completeMaintenance(record);
            refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (MaintenanceRecord r : maintenanceService.findAll()) {
            tableModel.addRow(new Object[]{
                    r.getRecordId(), r.getVehicle() != null ? r.getVehicle().getRegistrationNumber() : "-",
                    r.getMaintenanceType(), r.getDate(), r.getDescription(), r.getCost(),
                    r.isCompleted() ? "Yes" : "No"
            });
        }
    }

    public void refresh() { refreshTable(); }
}
