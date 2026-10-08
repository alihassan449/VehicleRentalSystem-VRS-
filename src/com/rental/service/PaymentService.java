package com.rental.service;

import java.time.LocalDateTime;

import com.rental.enums.PaymentMethod;
import com.rental.model.Payment;
import com.rental.model.RentalAgreement;
import com.rental.repository.PaymentRepository;
import com.rental.util.IdGenerator;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) { this.paymentRepository = paymentRepository; }

    public Payment recordPayment(RentalAgreement agreement, double amount, PaymentMethod method) {
        if (agreement == null) throw new IllegalArgumentException("Rental agreement is required");
        if (amount <= 0) throw new IllegalArgumentException("Payment amount must be positive");
        String id = IdGenerator.next("PAY");
        Payment payment = new Payment(id, agreement, amount, LocalDateTime.now(), method);
        paymentRepository.save(payment);
        agreement.addPayment(payment);
        return payment;
    }
}
