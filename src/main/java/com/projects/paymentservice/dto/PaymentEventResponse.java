package com.projects.paymentservice.dto;

import com.projects.paymentservice.enums.PaymentEventType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEventResponse {
    private Long id;
    private Long paymentId;
    private Long userId;
    private String idempotencyKey;
    private PaymentEventType eventType;
    private String details;
    private LocalDateTime createdAt;
}