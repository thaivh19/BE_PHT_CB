package com.pht.model.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChukySoCreateRequest {

    @NotBlank(message = "Serial number không được để trống")
    private String serialNumber;

    private String subject;

    private String issuer;

    @NotNull(message = "Ngày hiệu lực từ không được để trống")
    private LocalDateTime validFrom;

    @NotNull(message = "Ngày hiệu lực đến không được để trống")
    private LocalDateTime validTo;

    @NotBlank(message = "Mã doanh nghiệp không được để trống")
    private String maDoanhNghiep;

    private String tenDoanhNghiep;

    private String maSoThue;

    @NotBlank(message = "Dữ liệu chứng thư không được để trống")
    private String certificateData;

    private String privateKey;

    private String publicKey;

    private String trangThai;

    private String loaiChuKy;

    private String ghiChu;

    private String password;

    private Boolean isActive = true;

    private Boolean isDefault = false;
}
