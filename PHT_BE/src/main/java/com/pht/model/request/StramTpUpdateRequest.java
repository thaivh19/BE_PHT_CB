package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class StramTpUpdateRequest {

    private Long id;
    private String maTramTp;
    private String tenTramTp;
    private String masothue;
    private String diaChi;
    private String tenGiaoDich;
    private String trangThai;
}
