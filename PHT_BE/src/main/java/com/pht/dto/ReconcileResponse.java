package com.pht.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconcileResponse {
    private String code;
    private String message;
    private Object data;
    
    public static ReconcileResponse success(String message) {
        return ReconcileResponse.builder()
                .code("00")
                .message(message)
                .data(null)
                .build();
    }
    
    public static ReconcileResponse success(String message, Object data) {
        return ReconcileResponse.builder()
                .code("00")
                .message(message)
                .data(data)
                .build();
    }
}




