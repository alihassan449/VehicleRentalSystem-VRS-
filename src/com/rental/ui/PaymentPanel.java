package com.rental.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.rental.enums.PaymentMethod;
import com.rental.model.Payment;
import com.rental.model.RentalAgreement;
import com.rental.report.Receipt;
import com.rental.service.PaymentService;
import com.rental.service.RentalService;

public class PaymentPanel extends JPanel {

    private final PaymentService paymentService;
    private final RentalService rentalService;

    private final JTextField agreementIdField = new JTextField(10);
    private final JTextField amountField = new JTextField(8);
    private final JComboBox<PaymentMethod> methodBox = new JComboBox<>(PaymentMethod.values());

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Agreement ID", "Final Amount", "Paid So Far", "Balance Due"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public PaymentPanel(PaymentService paymentService, RentalService rentalService) {
        this.paymentService = paymentService;
        this.rentalService = rentalService;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        add(refreshBtn, BorderLayout.SOUTH);

        refreshTable();
        UiTheme.style(this);
        UiTheme.styleTable(table);
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Record Payment (FR24)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Agreement ID:"), gbc);
        gbc.gridx = 1; form.add(agreementIdField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; form.add(amountField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Method:"), gbc);
        gbc.gridx = 1; form.add(methodBox, gbc);
        row++;

        JButton payBtn = new JButton("Record Payment & Print Receipt (FR25)");
        payBtn.addActionListener(e -> recordPayment());
        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(payBtn, gbc);

        return form;
    }

    private void recordPayment() {
        try {
            RentalAgreement agreement = rentalService.findAll().stream()
                    .filter(a -> a.getAgreementId().equals(agreementIdField.getText().trim()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("No rental agreement with that ID"));

            double amount = Double.parseDouble(amountField.getText().trim());
            PaymentMethod method = (PaymentMethod) methodBox.getSelectedItem();

            Payment payment = paymentService.recordPayment(agreement, amount, method);
            JOptionPane.showMessageDialog(this, new Receipt(payment).render(), "Payment Recorded - Receipt", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (RentalAgreement a : rentalService.findAll()) {
            tableModel.addRow(new Object[]{ a.getAgreementId(), a.getFinalAmount(), a.totalPaid(), a.balanceDue() });
        }
    }

    public void refresh() { refreshTable(); }
}
