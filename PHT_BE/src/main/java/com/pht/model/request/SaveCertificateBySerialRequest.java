package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request đơn giản để lưu chữ ký số từ Windows Security bằng SerialNumber")
public class SaveCertificateBySerialRequest {
    
    @Schema(description = "Serial number của certificate trong Windows Security", example = "ABC123DEF456", required = true)
    private String serialNumber;
}
