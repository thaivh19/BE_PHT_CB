package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SloaiHinhSearchRequest {

    private String nhomLoaiHinh;
    private String maLoaiHinh;
    private String tenLoaiHinh;
    private String trangThai;
}
