package com.pht.model.response;

import lombok.Data;

@Data
public class PageResponse {
	private int pageNumber;
    private int pageSize;
    private int totalPages;
    private int numberOfElements;
    private long totalElements;
    private long totalAmount;
    
}
