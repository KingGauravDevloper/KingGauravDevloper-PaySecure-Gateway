package com.paysecure.orchestration.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String status;
    private String message;
    private String transactionId;
}
