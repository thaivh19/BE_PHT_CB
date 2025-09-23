package com.pht.model.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChukySoUpdateRequest {

    @NotNull(message = "ID không được để trống")
    private Long id;

    private String serialNumber;

    private String subject;

    private String issuer;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private String maDoanhNghiep;

    private String tenDoanhNghiep;

    private String maSoThue;

    private String certificateData;

    private String privateKey;

    private String publicKey;

    private String trangThai;

    private String loaiChuKy;

    private String ghiChu;

    private String password;

    private Boolean isActive;

    private Boolean isDefault;
}
