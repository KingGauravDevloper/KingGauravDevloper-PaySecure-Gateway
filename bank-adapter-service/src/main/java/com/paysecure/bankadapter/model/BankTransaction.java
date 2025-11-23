package com.paysecure.bankadapter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gatewayTransactionId; // links to payment gateway transaction
    private String bankId;
    private Double amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    public enum TransactionStatus {
        INITIATED, AUTHORIZED, CAPTURED, FAILED, PENDING
    }

    private String responseMessage;
}
