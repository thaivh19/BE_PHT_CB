package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SloaiThanhToanUpdateRequest {

    private Long id;
    private String maLoaiThanhToan;
    private String tenLoaiThanhToan;
    private String dienGiai;
    private String trangThai;
}
