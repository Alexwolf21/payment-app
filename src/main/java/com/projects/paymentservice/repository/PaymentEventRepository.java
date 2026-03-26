package com.projects.paymentservice.repository;

import com.projects.paymentservice.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {
    List<PaymentEvent> findByPayment_IdOrderByCreatedAtAsc(Long paymentId);
}