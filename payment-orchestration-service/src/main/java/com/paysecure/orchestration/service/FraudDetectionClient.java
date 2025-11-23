package com.paysecure.orchestration.service;

import com.paysecure.orchestration.dto.FraudCheckResponse; // Make sure you have this DTO
import com.paysecure.orchestration.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Component
public class FraudDetectionClient {

    private final WebClient webClient;

    @Value("${services.fraud-detection.url}")
    private String fraudServiceUrl;

    public FraudDetectionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Float getFraudScore(Transaction transaction) {
        try {
            // 1. Call Python API and expect the Full Object (FraudCheckResponse)
            FraudCheckResponse response = webClient.post()
                    .uri(fraudServiceUrl)
                    .bodyValue(transaction)
                    .retrieve()
                    .bodyToMono(FraudCheckResponse.class) // <--- CRITICAL FIX
                    .timeout(Duration.ofSeconds(5))
                    .block();

            // 2. Extract just the score
            if (response != null) {
                return response.getFraudScore();
            }
        } catch (Exception e) {
            // Log the error but return a safe default so the transaction doesn't crash
            System.err.println("⚠️ Fraud Check Failed: " + e.getMessage());
        }
        return 0.0f; // Default safe score if service is down
    }
}