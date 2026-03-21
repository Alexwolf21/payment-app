package com.projects.paymentservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateRequest {
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String recipientName;
    private String idempotencyKey;
}
