package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request để lưu chữ ký số từ frontend")
public class SaveCertificateFromFrontendRequest {

    @Schema(description = "Serial number của certificate", example = "ABC123DEF456", required = true)
    private String serialNumber;

    @Schema(description = "Tên chữ ký số", example = "Công ty TNHH ABC")
    private String name;

    @Schema(description = "Nhà phát hành certificate", example = "CA2")
    private String issuer;

    @Schema(description = "Subject của certificate", example = "CN=Công ty TNHH ABC, O=ABC Company")
    private String subject;

    @Schema(description = "Ngày hiệu lực từ (yyyy-MM-dd)", example = "2024-01-01")
    private String validFrom;

    @Schema(description = "Ngày hiệu lực đến (yyyy-MM-dd)", example = "2025-12-31")
    private String validTo;

    @Schema(description = "Dữ liệu certificate đã encode Base64", required = true)
    private String certificateData;

    @Schema(description = "Dữ liệu private key đã encode Base64")
    private String privateKeyData;

    @Schema(description = "Dữ liệu public key đã encode Base64")
    private String publicKeyData;

    @Schema(description = "Mã số thuế của doanh nghiệp", example = "0123456789")
    private String maSoThue;

    @Schema(description = "Tên doanh nghiệp", example = "Công ty TNHH ABC")
    private String tenDoanhNghiep;

    @Schema(description = "Mã doanh nghiệp", example = "DN_ABC123")
    private String maDoanhNghiep;

    @Schema(description = "Thuật toán ký", example = "RSA-PSS")
    private String signatureAlgorithm;

    @Schema(description = "Thuật toán hash", example = "SHA-256")
    private String hashAlgorithm;

    @Schema(description = "Thumbprint của certificate")
    private String thumbprint;

    @Schema(description = "Mật khẩu certificate")
    private String password;

    @Schema(description = "Ghi chú")
    private String ghiChu;
}




