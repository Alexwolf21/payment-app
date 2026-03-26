package com.projects.paymentservice.service;

import com.projects.paymentservice.dto.PaymentEventResponse;
import com.projects.paymentservice.entity.Payment;
import com.projects.paymentservice.entity.PaymentEvent;
import com.projects.paymentservice.enums.PaymentEventType;
import com.projects.paymentservice.repository.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentEventService {

    private final PaymentEventRepository paymentEventRepository;

    @Transactional
    public void recordEvent(Payment payment, PaymentEventType eventType, String details) {
        PaymentEvent event = PaymentEvent.builder()
                .payment(payment)
                .userId(payment != null && payment.getUser() != null ? payment.getUser().getId() : null)
                .idempotencyKey(payment != null ? payment.getIdempotencyKey() : null)
                .eventType(eventType)
                .details(details)
                .createdAt(LocalDateTime.now())
                .build();

        paymentEventRepository.save(event);
    }

    @Transactional
    public void recordRejectedEvent(Long userId, String idempotencyKey, String details) {
        PaymentEvent event = PaymentEvent.builder()
                .payment(null)
                .userId(userId)
                .idempotencyKey(idempotencyKey)
                .eventType(PaymentEventType.REJECTED)
                .details(details)
                .createdAt(LocalDateTime.now())
                .build();

        paymentEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<PaymentEventResponse> getEventsForPayment(Long paymentId) {
        return paymentEventRepository.findByPayment_IdOrderByCreatedAtAsc(paymentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentEventResponse toResponse(PaymentEvent event) {
        return PaymentEventResponse.builder()
                .id(event.getId())
                .paymentId(event.getPayment() != null ? event.getPayment().getId() : null)
                .userId(event.getUserId())
                .idempotencyKey(event.getIdempotencyKey())
                .eventType(event.getEventType())
                .details(event.getDetails())
                .createdAt(event.getCreatedAt())
                .build();
    }
}