package com.rental.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.rental.model.Address;
import com.rental.model.Customer;
import com.rental.model.DrivingLicense;
import com.rental.service.CustomerService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomerPanel extends JPanel {

    private final CustomerService customerService;

    private final JTextField nameField = new JTextField(15);
    private final JTextField contactField = new JTextField(12);
    private final JTextField nationalIdField = new JTextField(12);
    private final JTextField addressField = new JTextField(18);
    private final JTextField licenseNumberField = new JTextField(10);
    private final JTextField licenseExpiryField = new JTextField(10);

    private final JTextField searchField = new JTextField(15);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Customer ID", "Name", "Contact", "National ID", "License Expiry", "Eligible?"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public CustomerPanel(CustomerService customerService) {
        this.customerService = customerService;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildSearchPanel(), BorderLayout.SOUTH);

        refreshTable(customerService.findAll());
        UiTheme.style(this);
        UiTheme.styleTable(table);
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Register Customer (FR1)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(form, gbc, row++, "Name:", nameField);
        addRow(form, gbc, row++, "Contact number:", contactField);
        addRow(form, gbc, row++, "National ID:", nationalIdField);
        addRow(form, gbc, row++, "Address:", addressField);
        addRow(form, gbc, row++, "License number:", licenseNumberField);
        addRow(form, gbc, row++, "License expiry (yyyy-MM-dd):", licenseExpiryField);

        JButton registerBtn = new JButton("Register Customer");
        registerBtn.addActionListener(e -> registerCustomer());
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
        panel.setBorder(BorderFactory.createTitledBorder("Search by ID, National ID or Name (FR2)"));
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Show All");
        searchBtn.addActionListener(e -> refreshTable(customerService.searchCustomers(searchField.getText().trim())));
        clearBtn.addActionListener(e -> refreshTable(customerService.findAll()));
        panel.add(new JLabel("Keyword:"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(clearBtn);
        return panel;
    }

    private void registerCustomer() {
        try {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String nationalId = nationalIdField.getText().trim();
            Address address = new Address(addressField.getText().trim(), "", "", "", "");

            DrivingLicense license = null;
            if (!licenseNumberField.getText().trim().isEmpty()) {
                LocalDate expiry = LocalDate.parse(licenseExpiryField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                license = new DrivingLicense(licenseNumberField.getText().trim(), "", LocalDate.now(), expiry);
            }

            Customer customer = customerService.registerCustomer(name, address, contact, nationalId, license);
            JOptionPane.showMessageDialog(this, "Registered customer " + customer.getCustomerId());
            clearForm();
            refreshTable(customerService.findAll());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "License expiry must be in yyyy-MM-dd format", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        contactField.setText("");
        nationalIdField.setText("");
        addressField.setText("");
        licenseNumberField.setText("");
        licenseExpiryField.setText("");
    }

    private void refreshTable(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer c : customers) {
            String expiry = c.getDrivingLicense() != null ? String.valueOf(c.getDrivingLicense().getExpiryDate()) : "-";
            boolean eligible = customerService.isEligibleForRental(c);
            tableModel.addRow(new Object[]{
                    c.getCustomerId(), c.getName(), c.getContactNumber(), c.getNationalId(),
                    expiry, eligible ? "Yes" : "No (expired)"
            });
        }
    }

    public void refresh() { refreshTable(customerService.findAll()); }
}
