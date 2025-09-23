package com.pht.common.model.request;

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
public class QueryRequest<T> {

    private T sample;
    private PageInfo pageInfo;
    private List<OrderBy> orders;
}
