package com.paysecure.transactionservice.util;

import com.paysecure.transactionservice.dto.CreateTransactionRequest;
import com.paysecure.transactionservice.dto.TransactionResponse;
import com.paysecure.transactionservice.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    
    public Transaction toEntity(CreateTransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUserId(request.getUserId());
        transaction.setMerchantId(request.getMerchantId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setDescription(request.getDescription());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setCustomerEmail(request.getCustomerEmail());
        transaction.setCustomerPhone(request.getCustomerPhone());
        return transaction;
    }
    
    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUserId())
                .merchantId(transaction.getMerchantId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .paymentMethod(transaction.getPaymentMethod())
                .customerEmail(transaction.getCustomerEmail())
                .customerPhone(transaction.getCustomerPhone())
                .failureReason(transaction.getFailureReason())
                .fraudScore(transaction.getFraudScore())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .completedAt(transaction.getCompletedAt())
                .build();
    }
}
