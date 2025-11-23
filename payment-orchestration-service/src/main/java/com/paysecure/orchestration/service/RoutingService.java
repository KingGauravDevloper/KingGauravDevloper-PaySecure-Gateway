package com.paysecure.orchestration.service;

import com.paysecure.orchestration.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class RoutingService {

    public String routeProvider(Transaction transaction) {
        // Basic routing logic based on fraud score
        if (transaction.getFraudScore() != null && transaction.getFraudScore() > 0.5f) {
            return "HighRiskProvider"; // Placeholder provider name
        }
        return "DefaultProvider"; // Placeholder provider name
    }
}
