package com.paysecure.orchestration.service;

import com.paysecure.orchestration.exception.PaymentException;
import com.paysecure.orchestration.model.Transaction;
import com.paysecure.orchestration.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; // <--- NEW IMPORT
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // <--- NEW IMPORT
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j // Enables logging
public class OrchestrationService {

    private final TransactionRepository transactionRepository;
    private final FraudDetectionClient fraudDetectionClient;
    private final BankAdapterClient bankAdapterClient;
    private final RoutingService routingService;

    @Transactional
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackProcessPayment") // <--- FIX: Circuit Breaker
    public Transaction processPayment(Transaction paymentRequest) {
        
        // 1. Initialize
        paymentRequest.setStatus("PENDING");
        Transaction savedTransaction = transactionRepository.save(paymentRequest);

        try {
            // 2. Fraud Check
            log.info("Checking fraud for TXN: {}", savedTransaction.getTransactionId());
            Float fraudScore = fraudDetectionClient.getFraudScore(savedTransaction);
            savedTransaction.setFraudScore(fraudScore);

            // 3. Routing
            String provider = routingService.routeProvider(savedTransaction);
            savedTransaction.setProvider(provider);

            // 4. Bank Call
            log.info("Routing to provider: {}", provider);
            boolean success = bankAdapterClient.processPayment(savedTransaction);

            // 5. Success Path
            if (success) {
                savedTransaction.setStatus("SUCCESS");
            } else {
                savedTransaction.setStatus("FAILED");
                savedTransaction.setResponseData("{\"reason\": \"Bank declined transaction\"}");
            }

        } catch (Exception e) {
            // <--- FIX: ZOMBIE TRANSACTION HANDLING
            // If ANY error happens (Network timeout, 500 error, null pointer), we catch it here.
            log.error("Error processing payment for TXN: {}", savedTransaction.getTransactionId(), e);
            
            savedTransaction.setStatus("FAILED");
            savedTransaction.setResponseData("{\"error\": \"" + e.getMessage() + "\"}");
            
            // We MUST save this status so the user knows it failed.
            transactionRepository.save(savedTransaction);
            
            // Re-throw so the Controller knows to send a 500/400 error, 
            // OR just return the failed transaction depending on your API design.
            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }

        return transactionRepository.save(savedTransaction);
    }

    // <--- FIX: FALLBACK METHOD
    // If the Circuit Breaker is OPEN (Service is down), this runs immediately.
    public Transaction fallbackProcessPayment(Transaction paymentRequest, Throwable t) {
        log.warn("Circuit Breaker triggered! Bank/Fraud service is down. Reason: {}", t.getMessage());
        
        // We create a temporary FAILED transaction object to return
        paymentRequest.setStatus("FAILED");
        paymentRequest.setResponseData("{\"reason\": \"Service temporarily unavailable. Please try again later.\"}");
        
        // Note: We usually don't save to DB in fallback if the DB itself is the issue, 
        // but here we assume DB is fine.
        return transactionRepository.save(paymentRequest);
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new PaymentException("Transaction not found: " + transactionId));
    }
}