package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShaiQuanUpdateRequest {

    private Long id;
    private String maHq;
    private String tenHq;
    private String dienGiai;
    private String trangThai;
}
