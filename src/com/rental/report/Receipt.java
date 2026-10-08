package com.rental.report;

import com.rental.model.Payment;

public class Receipt {
    private final Payment payment;

    public Receipt(Payment payment) { this.payment = payment; }

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("================ PAYMENT RECEIPT ================\n");
        sb.append("Receipt ID     : ").append(payment.getPaymentId()).append("\n");
        sb.append("Agreement ID   : ").append(payment.getRentalAgreement().getAgreementId()).append("\n");
        sb.append("Date           : ").append(payment.getPaymentDate()).append("\n");
        sb.append("Method         : ").append(payment.getMethod()).append("\n");
        sb.append(String.format("Amount Paid    : %.2f%n", payment.getAmountPaid()));
        sb.append("===================================================\n");
        return sb.toString();
    }
}
