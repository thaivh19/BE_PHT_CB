package com.pht.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestClientResponse {

    public static final int UNKNOWN_STATUS_CODE = -1;
    public static final String UNKNOWN_STATUS_TEXT = "UNKNOWN";

    private int rawStatusCode;
    private String statusText;
    private byte[] responseBody;
    private String responseBodyAsString;
}