package com.pht.model.request;

import lombok.Data;

@Data
public class NgayLvUpdateRequest {
    
    private Long id;
    private String ngayLv; // Ngày làm việc
    private String trangThai; // Trạng thái (1: Làm việc, 0: Nghỉ)
    private String cot; // Cột phân loại
}









