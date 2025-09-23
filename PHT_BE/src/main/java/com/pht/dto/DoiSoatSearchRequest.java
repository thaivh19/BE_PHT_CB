package com.pht.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DoiSoatSearchRequest {
    private LocalDate tuNgay; // Từ ngày
    private LocalDate denNgay; // Đến ngày
    private String nganHang; // Ngân hàng (optional)
    private String nhDs; // Trạng thái đối soát ngân hàng: "00", "01", "02", "03", "99"
    private String kbDs; // Trạng thái đối soát kho bạc: "00", "01", "02", "03", "99" 
    private String trangThai; // Trạng thái tổng thể: "00", "01", "02"
}




