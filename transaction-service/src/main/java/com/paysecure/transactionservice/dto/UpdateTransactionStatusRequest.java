package com.paysecure.transactionservice.dto;

import com.paysecure.transactionservice.model.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionStatusRequest {
    
    @NotNull(message = "Status is required")
    private TransactionStatus status;
    
    private String failureReason;
    
    private Double fraudScore;
}

