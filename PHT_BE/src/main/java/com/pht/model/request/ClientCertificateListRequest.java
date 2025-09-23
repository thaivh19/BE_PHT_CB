package com.pht.model.request;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request để gửi danh sách chữ ký số từ client")
public class ClientCertificateListRequest {
    
    @Schema(description = "Danh sách chữ ký số từ máy client", required = true)
    private List<ClientCertificateInfo> certificates;
    
    @Data
    @Schema(description = "Thông tin chữ ký số từ client")
    public static class ClientCertificateInfo {
        
        @Schema(description = "Serial number của certificate", example = "ABC123DEF456", required = true)
        private String serialNumber;
        
        @Schema(description = "Tên certificate", example = "Công ty TNHH ABC")
        private String name;
        
        @Schema(description = "Nhà phát hành", example = "CA2")
        private String issuer;
        
        @Schema(description = "Subject của certificate")
        private String subject;
        
        @Schema(description = "Ngày hiệu lực từ", example = "20/08/2018")
        private String validFrom;
        
        @Schema(description = "Ngày hiệu lực đến", example = "18/08/2025")
        private String validTo;
        
        @Schema(description = "Dữ liệu certificate (Base64)", required = true)
        private String certificateData;
        
        @Schema(description = "Dữ liệu private key (Base64)", required = true)
        private String privateKeyData;
        
        @Schema(description = "Dữ liệu public key (Base64)")
        private String publicKeyData;
        
        @Schema(description = "Mã số thuế", example = "0123456789")
        private String maSoThue;
        
        @Schema(description = "Tên doanh nghiệp", example = "Công ty TNHH ABC")
        private String tenDoanhNghiep;
    }
}




