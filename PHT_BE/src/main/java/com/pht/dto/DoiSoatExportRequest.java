package com.pht.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DoiSoatExportRequest {
    private LocalDate ngayDs; // Ngày đối soát
    private String nganHang; // Ngân hàng (optional, null = tất cả)
    private String loaiDoiSoat; // Loại đối soát: "NH" (ngân hàng), "KB" (kho bạc), null = tất cả
}




