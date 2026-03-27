package com.projects.paymentservice.service;

import com.projects.paymentservice.dto.PaymentCreateRequest;
import com.projects.paymentservice.dto.PaymentEventResponse;
import com.projects.paymentservice.dto.PaymentResponse;
import com.projects.paymentservice.entity.Payment;
import com.projects.paymentservice.enums.PaymentEventType;
import com.projects.paymentservice.enums.PaymentStatus;
import com.projects.paymentservice.exception.DuplicateResourceException;
import com.projects.paymentservice.exception.InvalidPaymentStateException;
import com.projects.paymentservice.exception.ResourceNotFoundException;
import com.projects.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventService paymentEventService;

    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        String idempotencyKey = request.getIdempotencyKey().trim();

        if (paymentRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            paymentEventService.recordRejectedEvent(
                    request.getPayerId(),
                    idempotencyKey,
                    "Rejected: duplicate idempotency key"
            );
            throw new DuplicateResourceException("Payment already exists for idempotency key: " + idempotencyKey);
        }

        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .payerId(request.getPayerId().trim())
                .amount(request.getAmount())
                .currency(request.getCurrency().trim().toUpperCase())
                .recipientName(request.getRecipientName().trim())
                .status(PaymentStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        paymentEventService.recordEvent(savedPayment, PaymentEventType.CREATED, "Payment created successfully");

        return mapToResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse confirmPayment(Long paymentId) {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment id");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            paymentEventService.recordEvent(payment, PaymentEventType.REJECTED, "Rejected: only PENDING payments can be confirmed");
            throw new InvalidPaymentStateException("Only PENDING payments can be confirmed");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        paymentEventService.recordEvent(updatedPayment, PaymentEventType.CONFIRMED, "Payment confirmed successfully");

        return mapToResponse(updatedPayment);
    }

    @Transactional
    public PaymentResponse failPayment(Long paymentId, String reason) {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment id");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            paymentEventService.recordEvent(payment, PaymentEventType.REJECTED, "Rejected: only PENDING payments can be failed");
            throw new InvalidPaymentStateException("Only PENDING payments can be failed");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        paymentEventService.recordEvent(
                updatedPayment,
                PaymentEventType.FAILED,
                reason == null || reason.trim().isEmpty() ? "Payment failed" : reason.trim()
        );

        return mapToResponse(updatedPayment);
    }

    public PaymentResponse getPaymentById(Long paymentId) {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment id");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        return mapToResponse(payment);
    }

    public List<PaymentEventResponse> getPaymentEvents(Long paymentId) {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment id");
        }

        return paymentEventService.getEventsForPayment(paymentId);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .payerId(payment.getPayerId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .recipientName(payment.getRecipientName())
                .status(payment.getStatus())
                .idempotencyKey(payment.getIdempotencyKey())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}