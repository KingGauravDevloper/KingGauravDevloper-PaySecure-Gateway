package com.paysecure.bankadapter.controller;

import com.paysecure.bankadapter.model.BankTransaction;
import com.paysecure.bankadapter.service.BankAdapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments") // Matches the Orchestrator's target URL
@RequiredArgsConstructor         // Generates constructor for final fields (Best Practice)
@Slf4j                           // Enables logging
public class BankController {

    private final BankAdapterService bankAdapterService;

    @PostMapping
    public ResponseEntity<Boolean> authorize(@RequestBody BankTransaction request) {
        log.info("🔌 [Bank Adapter] Incoming Payment Request | Gateway TXN ID: {} | Amount: {}", 
                 request.getGatewayTransactionId(), request.getAmount());

        try {
            // Call the service logic (which simulates latency and random failure)
            boolean isSuccess = bankAdapterService.processPayment(request);
            
            log.info("🔌 [Bank Adapter] Processing Complete. Result: {}", isSuccess ? "APPROVED" : "DECLINED");
            return ResponseEntity.ok(isSuccess);

        } catch (Exception e) {
            log.error("💥 [Bank Adapter] System Error processing payment", e);
            // We return 500 so the Orchestrator's Circuit Breaker knows something is wrong
            return ResponseEntity.internalServerError().body(false);
        }
    }
}