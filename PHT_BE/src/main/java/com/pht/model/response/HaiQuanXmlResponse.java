package com.pht.model.response;

import lombok.Data;

@Data
public class HaiQuanXmlResponse {
    
    private String xmlResponse; // XML String từ Hải quan
    private String message;     // Thông báo
    private boolean success;    // Trạng thái thành công
}
