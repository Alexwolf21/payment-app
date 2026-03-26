package com.projects.paymentservice.controller;

import com.projects.paymentservice.dto.PaymentCreateRequest;
import com.projects.paymentservice.dto.PaymentEventResponse;
import com.projects.paymentservice.dto.PaymentResponse;
import com.projects.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        return new ResponseEntity<>(paymentService.createPayment(request), HttpStatus.CREATED);
    }

    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }

    @PostMapping("/{paymentId}/fail")
    public ResponseEntity<PaymentResponse> failPayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(paymentService.failPayment(paymentId, reason));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/{paymentId}/events")
    public ResponseEntity<List<PaymentEventResponse>> getPaymentEvents(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentEvents(paymentId));
    }
}