package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pht.common.OrderBy;
import com.pht.common.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class CatalogSearchRequest {

    private String keyword;
    private String status;
    private String maHq;
    private String tenHq;
    private String dienGiai;
    private String trangThai;
    private PageInfo pageInfo;
    private List<OrderBy> orders;
}
