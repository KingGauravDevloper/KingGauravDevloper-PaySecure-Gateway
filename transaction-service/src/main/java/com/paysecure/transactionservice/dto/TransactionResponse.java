package com.paysecure.transactionservice.dto;

import com.paysecure.transactionservice.model.TransactionStatus;
import com.paysecure.transactionservice.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String transactionId;
    private Long userId;
    private Long merchantId;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    private TransactionType transactionType;
    private String description;
    private String paymentMethod;
    private String customerEmail;
    private String customerPhone;
    private String failureReason;
    private Double fraudScore;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;
}
