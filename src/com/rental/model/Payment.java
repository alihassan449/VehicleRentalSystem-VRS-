package com.rental.model;

import java.time.LocalDateTime;

import com.rental.enums.PaymentMethod;

public class Payment {
    private String paymentId;
    private RentalAgreement rentalAgreement;
    private double amountPaid;
    private LocalDateTime paymentDate;
    private PaymentMethod method;

    public Payment() {}
    public Payment(String paymentId, RentalAgreement rentalAgreement, double amountPaid,
                    LocalDateTime paymentDate, PaymentMethod method) {
        this.paymentId = paymentId; this.rentalAgreement = rentalAgreement;
        this.amountPaid = amountPaid; this.paymentDate = paymentDate; this.method = method;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public RentalAgreement getRentalAgreement() { return rentalAgreement; }
    public void setRentalAgreement(RentalAgreement rentalAgreement) { this.rentalAgreement = rentalAgreement; }
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    @Override
    public String toString() { return paymentId + " - " + String.format("%.2f", amountPaid) + " (" + method + ")"; }
}
