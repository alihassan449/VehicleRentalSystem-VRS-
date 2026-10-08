package com.rental.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.rental.model.Payment;
import com.rental.model.RentalAgreement;

public class InMemoryPaymentRepository implements PaymentRepository {
    private final List<Payment> payments = new ArrayList<>();

    @Override
    public void save(Payment payment) { payments.add(payment); }

    @Override
    public List<Payment> findByRentalAgreement(RentalAgreement agreement) {
        List<Payment> result = new ArrayList<>();
        if (agreement == null) return result;
        for (Payment p : payments)
            if (p.getRentalAgreement() != null && p.getRentalAgreement().getAgreementId().equals(agreement.getAgreementId()))
                result.add(p);
        return result;
    }

    @Override
    public List<Payment> findByDateRange(LocalDate from, LocalDate to) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments) {
            if (p.getPaymentDate() == null) continue;
            LocalDate d = p.getPaymentDate().toLocalDate();
            if (!d.isBefore(from) && !d.isAfter(to)) result.add(p);
        }
        return result;
    }

    @Override
    public List<Payment> findAll() { return new ArrayList<>(payments); }
}
