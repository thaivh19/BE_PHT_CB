package com.pht.model.response;

import lombok.Data;

@Data
public class ChuKySoResponse {

    private String serialNumber; // Serial number
    private String issuer;       // Issuer
    private String subject;      // Subject
    private String cert;         // Certificate data (base64)
    private String validFrom;    // Ngày hiệu lực từ
    private String validTo;      // Ngày hết hạn
}
