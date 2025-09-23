package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request để lưu chữ ký số từ Windows Security vào database")
public class SaveWindowsCertificateRequest {
    
    @Schema(description = "Serial number của certificate trong Windows Security", example = "ABC123DEF456", required = true)
    private String serialNumber;
    
    @Schema(description = "Mã doanh nghiệp", example = "DN001")
    private String maDoanhNghiep;
    
    @Schema(description = "Tên doanh nghiệp", example = "Công ty TNHH ABC")
    private String tenDoanhNghiep;
    
    @Schema(description = "Mã số thuế", example = "0123456789")
    private String maSoThue;
    
    @Schema(description = "Loại chữ ký", example = "PERSONAL", allowableValues = {"PERSONAL", "ORGANIZATION", "ENTERPRISE"})
    private String loaiChuKy;
    
    @Schema(description = "Ghi chú", example = "Chữ ký số từ Windows Security")
    private String ghiChu;
    
    @Schema(description = "Đặt làm chữ ký mặc định", example = "false")
    private Boolean isDefault = false;
    
    @Schema(description = "Trạng thái", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "EXPIRED", "REVOKED"})
    private String trangThai = "ACTIVE";
}




