package com.projects.paymentservice.service;

import com.projects.paymentservice.dto.PaymentCreateRequest;
import com.projects.paymentservice.dto.PaymentResponse;
import com.projects.paymentservice.entity.Payment;
import com.projects.paymentservice.entity.User;
import com.projects.paymentservice.enums.PaymentStatus;
import com.projects.paymentservice.repository.PaymentRepository;
import com.projects.paymentservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request){
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null");
        }

        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new IllegalArgumentException("Valid userId is required");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
            throw new IllegalArgumentException("Currency is required");
        }

        if (request.getRecipientName() == null || request.getRecipientName().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient name is required");
        }

        if (request.getIdempotencyKey() == null || request.getIdempotencyKey().trim().isEmpty()) {
            throw new IllegalArgumentException("Idempotency key is required");
        }

        String IdempotencyKey = request.getIdempotencyKey().trim();

        if(paymentRepository.findByIdempotencyKey(IdempotencyKey).isPresent()){
            throw new IllegalArgumentException("Payment already exists with idempotency key: " + IdempotencyKey);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .user(user)
                .amount(request.getAmount())
                .currency(request.getCurrency().trim().toUpperCase())
                .recipientName(request.getRecipientName().trim())
                .status(PaymentStatus.PENDING)
                .idempotencyKey(IdempotencyKey)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse confirmPayment(Long paymentId){
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment id");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment id not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only PENDING payments can be confirmed");
        }

        payment.setStatus(PaymentStatus.PENDING);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        return mapToResponse(updatedPayment);
    }

    public PaymentResponse getPaymentById(Long paymentId){
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment id");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment){
        return PaymentResponse.builder()
                .id(payment.getId())
                .userId(payment.getUser() != null ? payment.getUser().getId() : null)
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
