package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SdiaDiemLuuKhoUpdateRequest {

    private Long id;
    private String maDiaDiemLuuKho;
    private String tenDiaDiem;
    private String tenDiaDiemTcVN;
    private String diaDiem;
    private String dienGiai;
    private String trangThai;
    private String loai;
}
