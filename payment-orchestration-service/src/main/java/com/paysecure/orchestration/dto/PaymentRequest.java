package com.paysecure.orchestration.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String userId;
    private String merchantId;
    private BigDecimal amount;
    private String currency;
}
