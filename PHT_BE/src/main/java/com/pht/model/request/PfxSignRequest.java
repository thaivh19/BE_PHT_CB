package com.pht.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PfxSignRequest {
    
    @NotBlank(message = "Nội dung XML không được để trống")
    private String xmlContent;
    
    private String pfxFilePath = "C:/IDA/HQ/TPBANK.pfx";
    
    private String password = "123456";
}
