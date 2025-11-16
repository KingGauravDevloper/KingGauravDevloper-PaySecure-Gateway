package com.paysecure.transactionservice.controller;

import com.paysecure.transactionservice.dto.CreateTransactionRequest;
import com.paysecure.transactionservice.dto.TransactionResponse;
import com.paysecure.transactionservice.dto.UpdateTransactionStatusRequest;
import com.paysecure.transactionservice.model.TransactionStatus;
import com.paysecure.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        log.info("Received request to create transaction");
        TransactionResponse response = transactionService.createTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        log.info("Received request to fetch transaction by ID: {}", id);
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/txn/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionByTransactionId(
            @PathVariable String transactionId) {
        log.info("Received request to fetch transaction by transaction ID: {}", transactionId);
        TransactionResponse response = transactionService.getTransactionByTransactionId(transactionId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        log.info("Received request to fetch all transactions");
        List<TransactionResponse> responses = transactionService.getAllTransactions();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(@PathVariable Long userId) {
        log.info("Received request to fetch transactions for user ID: {}", userId);
        List<TransactionResponse> responses = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByMerchantId(
            @PathVariable Long merchantId) {
        log.info("Received request to fetch transactions for merchant ID: {}", merchantId);
        List<TransactionResponse> responses = transactionService.getTransactionsByMerchantId(merchantId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByStatus(
            @PathVariable TransactionStatus status) {
        log.info("Received request to fetch transactions with status: {}", status);
        List<TransactionResponse> responses = transactionService.getTransactionsByStatus(status);
        return ResponseEntity.ok(responses);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<TransactionResponse> updateTransactionStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionStatusRequest request) {
        log.info("Received request to update transaction status for ID: {}", id);
        TransactionResponse response = transactionService.updateTransactionStatus(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("Received request to delete transaction with ID: {}", id);
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
