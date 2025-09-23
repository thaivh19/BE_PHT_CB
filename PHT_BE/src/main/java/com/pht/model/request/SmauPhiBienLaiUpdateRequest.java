package com.pht.model.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SmauPhiBienLaiUpdateRequest {

    private Long id;
    private String mauBienLai;
    private String kyHieu;
    private String tuSo;
    private String denSo;
    private LocalDate ngayHieuLuc;
    private String diemThuPhi;
    private String trangThai;
    private String phatHanh;
    private String nguoiTao;
}
