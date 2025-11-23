package com.paysecure.transactionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEvent {
    private String transactionId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private String email;
    private String phone;
    private String type; // "CREATED", "UPDATED"
}