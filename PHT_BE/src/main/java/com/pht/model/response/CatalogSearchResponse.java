package com.pht.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogSearchResponse<T> {

    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private int numberOfElements;
    private long totalElements;
    private List<T> content;
    private String searchKeyword;
    private long searchTime; // milliseconds
}
