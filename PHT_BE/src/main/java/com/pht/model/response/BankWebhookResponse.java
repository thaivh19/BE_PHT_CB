package com.pht.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankWebhookResponse {

    private String transId;
    private String providerId;
    private String errorCode;
    private String errorDesc;
    private String signature;
}








