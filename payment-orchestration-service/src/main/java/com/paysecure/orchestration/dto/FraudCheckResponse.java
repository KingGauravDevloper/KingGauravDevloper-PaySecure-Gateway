package com.paysecure.orchestration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudCheckResponse {
    
    @JsonProperty("transaction_id")
    private String transactionId;
    
    @JsonProperty("fraud_score")
    private Float fraudScore;
    
    @JsonProperty("is_fraudulent")
    private boolean isFraudulent;
    
    @JsonProperty("risk_level")
    private String riskLevel;
    
    private String details;
}
