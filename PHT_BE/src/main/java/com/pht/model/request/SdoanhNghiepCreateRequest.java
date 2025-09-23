package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SdoanhNghiepCreateRequest {

    private String maDn;
    private String tenDn;
    private String dienGiai;
    private String trangThai;
}
