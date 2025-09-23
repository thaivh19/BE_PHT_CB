package com.pht.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pht.common.model.ApiDataResponse;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBy {

    @NotEmpty(message = "Property must not be null!")
    private String property;

    @NotEmpty(message = "Direction must not be null!")
    private String direction;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(callSuper = true)
    public static class ApiErrorResponse<T> extends ApiDataResponse<T> {

        private List<String> errors;
        private String error;

        @Builder(builderMethodName = "apiErrorResponseBuilder")
        public ApiErrorResponse(HttpStatus status, LocalDateTime timestamp, String message, T data,
                                List<String> errors, String error, Long startTime, Long endTime, Long executionTime,
                                String requestId, String path) {
            super(status.value(), requestId, timestamp, startTime, endTime, executionTime, message, path, data);
            this.errors = errors;
            this.error = error;
        }
    }
}
