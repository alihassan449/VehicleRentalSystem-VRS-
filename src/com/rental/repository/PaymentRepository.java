package com.rental.repository;

import java.time.LocalDate;
import java.util.List;

import com.rental.model.Payment;
import com.rental.model.RentalAgreement;

public interface PaymentRepository {
    void save(Payment payment);
    List<Payment> findByRentalAgreement(RentalAgreement agreement);
    List<Payment> findByDateRange(LocalDate from, LocalDate to);
    List<Payment> findAll();
}
