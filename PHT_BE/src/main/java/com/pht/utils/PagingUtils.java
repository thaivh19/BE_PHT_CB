package com.pht.utils;

import org.springframework.data.domain.Page;

import com.pht.common.PageInfo;
import com.pht.common.model.response.PagingResponse;

public class PagingUtils {

	public static <T> PagingResponse<T> buildPagingResponse(Page<T> page, PageInfo pageInfo) {
		return PagingResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(QueryUtils.getPageNumber(pageInfo))
                .pageSize(QueryUtils.getPageSize(pageInfo, page.getContent()))
                .numberOfElements(QueryUtils.getNumberOfElements(page.getContent()))
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
	}
}
