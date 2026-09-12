package com.bookstore.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SepayWebhookRequest {
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String subAccount;
    private String code;
    private String content; 
    private String transferType; 
    private BigDecimal transferAmount;
    private BigDecimal accumulated;
    private String channel;
    private String referenceCode;
}
