package com.pht.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelTraCuuResponse {
    
    private int totalRows; // Tổng số dòng trong Excel
    private int processedRows; // Số dòng đã xử lý thành công
    private int foundRows; // Số dòng tìm thấy kết quả
    private int notFoundRows; // Số dòng không tìm thấy kết quả
    private List<ToKhaiTraCuuResponse> data; // Danh sách kết quả tra cứu (chỉ những dòng tìm thấy)
    private List<ExcelRowError> errors; // Danh sách lỗi từng dòng (những dòng không tìm thấy)
    
    @Data
    @NoArgsConstructor
    public static class ExcelRowError {
        private int rowNumber; // Số dòng trong Excel (bắt đầu từ 1)
        private String soVanDon; // SO_VANDON từ Excel
        private String soHieu; // SO_HIEU từ Excel
        private String errorMessage; // Thông báo lỗi
        
        public ExcelRowError(int rowNumber, String soVanDon, String soHieu, String errorMessage) {
            this.rowNumber = rowNumber;
            this.soVanDon = soVanDon;
            this.soHieu = soHieu;
            this.errorMessage = errorMessage;
        }
    }
}
