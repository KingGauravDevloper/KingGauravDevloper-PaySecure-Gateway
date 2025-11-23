package com.paysecure.orchestration.service;

import com.paysecure.orchestration.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Component
public class BankAdapterClient {

    private final WebClient webClient;

    @Value("${services.bank-adapter.url}")
    private String bankApiUrl;

    public BankAdapterClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public boolean processPayment(Transaction transaction) {
        // Simulate calling bank API for payment
        return webClient.post()
                .uri(bankApiUrl)
                .bodyValue(transaction)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
