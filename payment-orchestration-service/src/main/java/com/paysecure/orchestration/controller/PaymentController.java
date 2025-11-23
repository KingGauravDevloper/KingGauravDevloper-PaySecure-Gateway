package com.paysecure.orchestration.controller;

import com.paysecure.orchestration.model.Transaction;
import com.paysecure.orchestration.service.OrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final OrchestrationService orchestrationService;

    @PostMapping
    public ResponseEntity<Transaction> createPayment(@RequestBody Transaction paymentRequest) {
        Transaction transaction = orchestrationService.processPayment(paymentRequest);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getPayment(@PathVariable UUID id) {
        Transaction transaction = orchestrationService.getTransaction(id);
        return ResponseEntity.ok(transaction);
    }
}
