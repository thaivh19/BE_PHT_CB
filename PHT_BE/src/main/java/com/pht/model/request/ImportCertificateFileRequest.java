package com.pht.model.request;

import lombok.Data;

@Data
public class ImportCertificateFileRequest {
    
    /**
     * Đường dẫn file certificate (.p12, .pfx, .crt, .pem, .cer) - BẮT BUỘC
     */
    private String certificateFilePath;
    
    /**
     * Mật khẩu file (.p12/.pfx) hoặc private key - TÙY CHỌN
     */
    private String password;
    
    /**
     * Có phải chữ ký mặc định không - TÙY CHỌN (default: false)
     */
    private Boolean isDefault = false;
}
