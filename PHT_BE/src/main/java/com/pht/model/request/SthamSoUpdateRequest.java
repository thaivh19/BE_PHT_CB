package com.pht.model.request;

import lombok.Data;

@Data
public class SthamSoUpdateRequest {
    
    private Long id;
    private String maTs; // Mã tham số
    private String tenTs; // Tên tham số
    private String giaTri; // Giá trị tham số
}









