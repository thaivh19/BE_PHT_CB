package com.pht.dto;

import lombok.Data;

@Data
public class ExcelTraCuuRequest {
    
    private String fileName;
    private String contentType;
    private byte[] fileData;
    
    public ExcelTraCuuRequest() {}
    
    public ExcelTraCuuRequest(String fileName, String contentType, byte[] fileData) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileData = fileData;
    }
}


