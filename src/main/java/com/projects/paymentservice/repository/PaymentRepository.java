package com.projects.paymentservice.repository;

import com.projects.paymentservice.entity.Payment;
import com.projects.paymentservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdempotencyKey(String IdempotencyKey);
    List<Payment> findByStatus(PaymentStatus status);
}
