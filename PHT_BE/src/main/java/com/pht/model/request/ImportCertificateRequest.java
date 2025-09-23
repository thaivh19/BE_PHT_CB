package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request để import chữ ký số từ file certificate")
public class ImportCertificateRequest {
    
    @Schema(description = "Nội dung file certificate (Base64 encoded)", example = "-----BEGIN CERTIFICATE-----...")
    private String certificateData;
    
    @Schema(description = "Nội dung file private key (Base64 encoded)", example = "-----BEGIN PRIVATE KEY-----...")
    private String privateKeyData;
    
    @Schema(description = "Mật khẩu private key (nếu có)", example = "123456")
    private String password;
    
    @Schema(description = "Mã doanh nghiệp", example = "DN001")
    private String maDoanhNghiep;
    
    @Schema(description = "Tên doanh nghiệp", example = "Công ty TNHH ABC")
    private String tenDoanhNghiep;
    
    @Schema(description = "Mã số thuế", example = "0123456789")
    private String maSoThue;
    
    @Schema(description = "Loại chữ ký", example = "PERSONAL", allowableValues = {"PERSONAL", "ORGANIZATION", "ENTERPRISE"})
    private String loaiChuKy;
    
    @Schema(description = "Ghi chú", example = "Chữ ký số cho ký tờ khai")
    private String ghiChu;
    
    @Schema(description = "Đặt làm chữ ký mặc định", example = "false")
    private Boolean isDefault = false;
}


