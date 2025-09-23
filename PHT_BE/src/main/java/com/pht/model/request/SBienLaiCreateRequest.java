package com.pht.model.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SBienLaiCreateRequest {

    private String mst;
    private String tenDvi;
    private String diaChi;
    private String email;
    private String sdt;
    private String maBl;
    private String soBl;
    private String hthucTtoan;
    private LocalDateTime ngayBl;
    private String loaiCtiet;
    private String ghiChu;
    private String stb;
    private LocalDateTime ngayNop;
    private String soTk;
    private LocalDateTime ngayTk;
    private String maKho;
    private String nguoiTao;
    private String idPhatHanh;
    private String imageBl;
    
    // ID tờ khai để cập nhật vào bảng StoKhai
    private Long toKhaiId;
    
    // Chi tiết biên lai
    private List<SBienLaiCtCreateRequest> chiTietList;
}
