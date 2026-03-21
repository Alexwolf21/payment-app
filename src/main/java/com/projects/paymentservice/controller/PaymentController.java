package com.projects.paymentservice.controller;

import com.projects.paymentservice.dto.PaymentCreateRequest;
import com.projects.paymentservice.dto.PaymentResponse;
import com.projects.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentCreateRequest payment) {
        PaymentResponse response = paymentService.createPayment(payment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.confirmPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

}
