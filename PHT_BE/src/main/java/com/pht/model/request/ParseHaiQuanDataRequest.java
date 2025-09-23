package com.pht.model.request;

import lombok.Data;

@Data
public class ParseHaiQuanDataRequest {
    
    private String haiQuanResponse; // Response String từ Hải quan
    private String soToKhaiHaiQuan; // Số tờ khai hải quan (optional)
    private String maDoanhNghiep;   // Mã doanh nghiệp (optional)
}
