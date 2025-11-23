package com.paysecure.transactionservice.service;

import com.paysecure.transactionservice.dto.CreateTransactionRequest;
import com.paysecure.transactionservice.dto.TransactionEvent;
import com.paysecure.transactionservice.dto.TransactionResponse;
import com.paysecure.transactionservice.dto.UpdateTransactionStatusRequest;
import com.paysecure.transactionservice.exception.ResourceNotFoundException;
import com.paysecure.transactionservice.model.Transaction;
import com.paysecure.transactionservice.model.TransactionStatus;
import com.paysecure.transactionservice.repository.TransactionRepository;
import com.paysecure.transactionservice.util.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final NotificationProducer notificationProducer; // <--- 1. Inject Producer

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        log.info("Creating new transaction for user: {}, merchant: {}", 
                 request.getUserId(), request.getMerchantId());

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setTransactionId(generateTransactionId());
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created successfully with ID: {}", savedTransaction.getTransactionId());

        // <--- 2. Trigger Kafka Event (Async Notification) --->
        try {
            TransactionEvent event = TransactionEvent.builder()
                    .transactionId(savedTransaction.getTransactionId())
                    .userId(savedTransaction.getUserId())
                    .amount(savedTransaction.getAmount())
                    .status(savedTransaction.getStatus().name())
                    .email(savedTransaction.getCustomerEmail())
                    .phone(savedTransaction.getCustomerPhone())
                    .type("CREATED")
                    .build();

            notificationProducer.sendNotification(event);
        } catch (Exception e) {
            // We log the error but do NOT throw it. 
            // We don't want to rollback the transaction just because the email failed.
            log.error("Failed to send CREATED notification for transaction: {}", 
                      savedTransaction.getTransactionId(), e);
        }

        return transactionMapper.toResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        log.info("Fetching transaction by ID: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return transactionMapper.toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByTransactionId(String transactionId) {
        log.info("Fetching transaction by transaction ID: {}", transactionId);
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with transaction ID: " + transactionId));
        return transactionMapper.toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        log.info("Fetching all transactions");
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        log.info("Fetching transactions for user ID: {}", userId);
        return transactionRepository.findByUserId(userId).stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByMerchantId(Long merchantId) {
        log.info("Fetching transactions for merchant ID: {}", merchantId);
        return transactionRepository.findByMerchantId(merchantId).stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByStatus(TransactionStatus status) {
        log.info("Fetching transactions with status: {}", status);
        return transactionRepository.findByStatus(status).stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionResponse updateTransactionStatus(Long id, UpdateTransactionStatusRequest request) {
        log.info("Updating transaction status for ID: {} to {}", id, request.getStatus());

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

        transaction.setStatus(request.getStatus());

        if (request.getFailureReason() != null) {
            transaction.setFailureReason(request.getFailureReason());
        }

        if (request.getFraudScore() != null) {
            transaction.setFraudScore(request.getFraudScore());
        }

        if (request.getStatus() == TransactionStatus.SUCCESS || 
            request.getStatus() == TransactionStatus.FAILED) {
            transaction.setCompletedAt(Instant.now());
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transaction status updated successfully for ID: {}", id);

        // <--- 3. Trigger Kafka Event (Async Notification) --->
        try {
            TransactionEvent event = TransactionEvent.builder()
                    .transactionId(updatedTransaction.getTransactionId())
                    .userId(updatedTransaction.getUserId())
                    .amount(updatedTransaction.getAmount())
                    .status(updatedTransaction.getStatus().name())
                    .email(updatedTransaction.getCustomerEmail())
                    .phone(updatedTransaction.getCustomerPhone())
                    .type("UPDATED")
                    .build();

            notificationProducer.sendNotification(event);
        } catch (Exception e) {
            log.error("Failed to send UPDATED notification for transaction: {}", 
                      updatedTransaction.getTransactionId(), e);
        }

        return transactionMapper.toResponse(updatedTransaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        log.info("Deleting transaction with ID: {}", id);
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
        log.info("Transaction deleted successfully with ID: {}", id);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}