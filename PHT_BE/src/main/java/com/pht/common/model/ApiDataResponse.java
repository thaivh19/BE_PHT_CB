package com.pht.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pht.constants.CommonConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiDataResponse<T> {

    private int status;

    private String requestId;

    @Builder.Default
    @JsonFormat(pattern = CommonConstants.DateTimePattern.FORMAT_24H)
    private LocalDateTime timestamp = LocalDateTime.now();

    private Long startTime;

    private Long endTime;

    private Long executionTime;

    private String message;

    private String path;

    private T data;
}