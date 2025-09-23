package com.pht.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response khi import chữ ký số thành công")
public class ImportCertificateResponse {
    
    @Schema(description = "ID của chữ ký số đã import", example = "123")
    private Long id;
    
    @Schema(description = "Serial number của certificate", example = "ABC123DEF456")
    private String serialNumber;
    
    @Schema(description = "Tên doanh nghiệp", example = "Công ty TNHH ABC")
    private String tenDoanhNghiep;
    
    @Schema(description = "Mã số thuế", example = "0123456789")
    private String maSoThue;
    
    @Schema(description = "Nhà phát hành certificate", example = "CA2")
    private String issuer;
    
    @Schema(description = "Ngày hiệu lực từ", example = "01/01/2024")
    private String validFrom;
    
    @Schema(description = "Ngày hiệu lực đến", example = "31/12/2025")
    private String validTo;
    
    @Schema(description = "Loại chữ ký", example = "PERSONAL")
    private String loaiChuKy;
    
    @Schema(description = "Trạng thái", example = "ACTIVE")
    private String trangThai;
    
    @Schema(description = "Thông báo kết quả", example = "Import chữ ký số thành công")
    private String message;
}




