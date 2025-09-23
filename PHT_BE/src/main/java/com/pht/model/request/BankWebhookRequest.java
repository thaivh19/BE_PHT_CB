package com.pht.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankWebhookRequest {

    @NotBlank(message = "msgId không được để trống")
    private String msgId;
    
    @NotBlank(message = "providerId không được để trống")
    private String providerId;
    
    @NotBlank(message = "transId không được để trống")
    private String transId;
    
    @NotBlank(message = "transTime không được để trống")
    private String transTime;
    
    private String transType;
    private String custCode;
    private String recvAcctId;
    private String recvAcctName;
    private String recvVirtualAcctId;
    private String recvVirtualAcctName;
    
    @NotBlank(message = "amount không được để trống")
    private String amount;
    
    private String bankTransId;
    private String remark;
    private String currencyCode;
    private String signature;
}
