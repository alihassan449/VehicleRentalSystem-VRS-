package com.rental.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.rental.enums.FuelType;
import com.rental.enums.TransmissionType;
import com.rental.enums.VehicleCategory;
import com.rental.enums.VehicleStatus;
import com.rental.model.Vehicle;
import com.rental.service.VehicleService;

public class VehiclePanel extends JPanel {

    private final VehicleService vehicleService;

    private final JTextField regField = new JTextField(10);
    private final JTextField makeField = new JTextField(10);
    private final JTextField modelField = new JTextField(10);
    private final JTextField yearField = new JTextField(5);
    private final JComboBox<VehicleCategory> categoryBox = new JComboBox<>(VehicleCategory.values());
    private final JComboBox<TransmissionType> transmissionBox = new JComboBox<>(TransmissionType.values());
    private final JComboBox<FuelType> fuelBox = new JComboBox<>(FuelType.values());
    private final JTextField mileageField = new JTextField(8);
    private final JTextField rateField = new JTextField(8);

    private final JComboBox<VehicleStatus> statusFilterBox = new JComboBox<>(VehicleStatus.values());
    private final JComboBox<VehicleCategory> categoryFilterBox = new JComboBox<>(VehicleCategory.values());
    private final JTextField regSearchField = new JTextField(10);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Reg. No.", "Make", "Model", "Year", "Category", "Transmission",
                    "Fuel", "Mileage", "Daily Rate", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public VehiclePanel(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildSearchPanel(), BorderLayout.SOUTH);

        refreshTable(vehicleService.findAll());
        UiTheme.style(this);
        UiTheme.styleTable(table);
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Register Vehicle (FR6, FR7)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(form, gbc, row++, "Registration number:", regField);
        addRow(form, gbc, row++, "Make:", makeField);
        addRow(form, gbc, row++, "Model:", modelField);
        addRow(form, gbc, row++, "Year:", yearField);
        addRow(form, gbc, row++, "Category:", categoryBox);
        addRow(form, gbc, row++, "Transmission:", transmissionBox);
        addRow(form, gbc, row++, "Fuel type:", fuelBox);
        addRow(form, gbc, row++, "Current mileage:", mileageField);
        addRow(form, gbc, row++, "Daily rental rate:", rateField);

        JButton registerBtn = new JButton("Register Vehicle");
        registerBtn.addActionListener(e -> registerVehicle());
        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(registerBtn, gbc);

        return form;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search / Filter (FR9)"));

        JButton byRegBtn = new JButton("By Reg. No.");
        byRegBtn.addActionListener(e -> refreshTable(vehicleService.searchByRegistrationNumber(regSearchField.getText().trim())));

        JButton byCategoryBtn = new JButton("By Category");
        byCategoryBtn.addActionListener(e -> refreshTable(vehicleService.searchByCategory((VehicleCategory) categoryFilterBox.getSelectedItem())));

        JButton byStatusBtn = new JButton("By Status");
        byStatusBtn.addActionListener(e -> refreshTable(vehicleService.searchByStatus((VehicleStatus) statusFilterBox.getSelectedItem())));

        JButton allBtn = new JButton("Show All");
        allBtn.addActionListener(e -> refreshTable(vehicleService.findAll()));

        panel.add(new JLabel("Reg. No.:"));
        panel.add(regSearchField);
        panel.add(byRegBtn);
        panel.add(categoryFilterBox);
        panel.add(byCategoryBtn);
        panel.add(statusFilterBox);
        panel.add(byStatusBtn);
        panel.add(allBtn);
        return panel;
    }

    private void registerVehicle() {
        try {
            String reg = regField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            double mileage = Double.parseDouble(mileageField.getText().trim());
            double rate = Double.parseDouble(rateField.getText().trim());

            Vehicle v = vehicleService.registerVehicle(reg, make, model, year,
                    (VehicleCategory) categoryBox.getSelectedItem(),
                    (TransmissionType) transmissionBox.getSelectedItem(),
                    (FuelType) fuelBox.getSelectedItem(), mileage, rate);

            JOptionPane.showMessageDialog(this, "Registered vehicle " + v.getRegistrationNumber());
            clearForm();
            refreshTable(vehicleService.findAll());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year, mileage and rate must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        regField.setText("");
        makeField.setText("");
        modelField.setText("");
        yearField.setText("");
        mileageField.setText("");
        rateField.setText("");
    }

    private void refreshTable(List<Vehicle> vehicles) {
        tableModel.setRowCount(0);
        for (Vehicle v : vehicles) {
            tableModel.addRow(new Object[]{
                    v.getRegistrationNumber(), v.getMake(), v.getModel(), v.getYear(),
                    v.getCategory(), v.getTransmissionType(), v.getFuelType(),
                    v.getCurrentMileage(), v.getDailyRentalRate(), v.getStatus()
            });
        }
    }

    public void refresh() { refreshTable(vehicleService.findAll()); }
}
