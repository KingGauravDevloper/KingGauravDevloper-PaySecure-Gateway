package com.paysecure.bankadapter.service;

import com.paysecure.bankadapter.model.BankTransaction;
import com.paysecure.bankadapter.model.BankTransaction.TransactionStatus; // Ensure this Import exists
import com.paysecure.bankadapter.repository.BankTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class BankAdapterService {

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    /**
     * Main method called by the Orchestrator.
     * Returns TRUE if payment is successful, FALSE if declined.
     */
    public boolean processPayment(BankTransaction request) {
        log.info("🏦 [Mock Bank] Received request for Amount: {} {}", request.getAmount(), request.getCurrency());

        // 1. Initial Save (Audit Log)
        request.setStatus(TransactionStatus.INITIATED);
        request.setBankId("MOCK-BANK-" + new Random().nextInt(9999));
        bankTransactionRepository.save(request);

        // 2. Simulate Network Latency (1 to 3 seconds)
        // This helps test the Timeout logic in your Orchestrator
        try {
            int delay = 1000 + new Random().nextInt(2000); 
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Bank processing interrupted");
        }

        // 3. Simulate Bank Logic (80% Success, 20% Failure)
        // This helps test the "FAILED" status in your Orchestrator
        boolean isSuccess = new Random().nextDouble() > 0.2;

        // 4. Update Status
        if (isSuccess) {
            request.setStatus(TransactionStatus.AUTHORIZED);
            request.setResponseMessage("Approved by Mock Bank System");
            log.info("✅ Payment Approved");
        } else {
            request.setStatus(TransactionStatus.FAILED);
            request.setResponseMessage("Declined: Insufficient Funds / Fraud Detected");
            log.info("❌ Payment Declined");
        }

        // 5. Save Final State
        bankTransactionRepository.save(request);

        return isSuccess;
    }
}