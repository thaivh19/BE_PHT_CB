package com.pht.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.dto.ExcelTraCuuResponse;
import com.pht.dto.ToKhaiTraCuuResponse;
import com.pht.exception.BusinessException;
import com.pht.service.ExcelTraCuuService;
import com.pht.service.ToKhaiTraCuuService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ExcelTraCuuServiceImpl implements ExcelTraCuuService {
    
    // Inner class để lưu dữ liệu từ Excel
    private static class ExcelRowData {
        private int rowNumber;
        private String soVanDon;
        private String soHieu;
        
        public ExcelRowData(int rowNumber, String soVanDon, String soHieu) {
            this.rowNumber = rowNumber;
            this.soVanDon = soVanDon;
            this.soHieu = soHieu;
        }
        
        public int getRowNumber() { return rowNumber; }
        public String getSoVanDon() { return soVanDon; }
        public String getSoHieu() { return soHieu; }
    }

    @Autowired
    private ToKhaiTraCuuService toKhaiTraCuuService;

    @Override
    public ExcelTraCuuResponse processExcelAndTraCuu(byte[] fileData, String fileName) throws BusinessException {
        log.info("Bắt đầu xử lý file Excel: {}", fileName);
        
        try {
            // Đọc file Excel
            List<ExcelRowData> rowDataList = readExcelFile(fileData);
            
            // Tạo Set để loại bỏ trùng lặp dựa trên SO_VANDON + SO_HIEU
            Set<String> uniqueKeys = new HashSet<>();
            List<ExcelRowData> uniqueRowDataList = new ArrayList<>();
            
            for (ExcelRowData rowData : rowDataList) {
                String key = (rowData.getSoVanDon() != null ? rowData.getSoVanDon().trim() : "") + 
                           "|" + (rowData.getSoHieu() != null ? rowData.getSoHieu().trim() : "");
                if (!uniqueKeys.contains(key)) {
                    uniqueKeys.add(key);
                    uniqueRowDataList.add(rowData);
                }
            }
            
            log.info("Từ {} dòng Excel, có {} cặp SO_VANDON+SO_HIEU duy nhất", 
                    rowDataList.size(), uniqueRowDataList.size());
            
            // Tra cứu thông tin cho từng cặp duy nhất
            List<ToKhaiTraCuuResponse> foundData = new ArrayList<>();
            List<ExcelTraCuuResponse.ExcelRowError> errors = new ArrayList<>();
            int foundRows = 0;
            int notFoundRows = 0;
            
            for (ExcelRowData rowData : uniqueRowDataList) {
                try {
                    List<ToKhaiTraCuuResponse> traCuuResults = toKhaiTraCuuService.traCuuToKhai(
                        rowData.getSoVanDon(), 
                        rowData.getSoHieu()
                    );
                    
                    if (!traCuuResults.isEmpty()) {
                        // Lấy tất cả kết quả tìm thấy
                        foundData.addAll(traCuuResults);
                        foundRows += traCuuResults.size();
                        log.debug("Cặp SO_VANDON={}, SO_HIEU={}: Tìm thấy {} kết quả", 
                                rowData.getSoVanDon(), rowData.getSoHieu(), traCuuResults.size());
                    } else {
                        // Tạo response với thông tin "chưa vào cảng"
                        ToKhaiTraCuuResponse notFoundResponse = createNotFoundResponse(rowData);
                        foundData.add(notFoundResponse);
                        foundRows++;
                        log.debug("Cặp SO_VANDON={}, SO_HIEU={}: Chưa vào cảng", 
                                rowData.getSoVanDon(), rowData.getSoHieu());
                    }
                    
                } catch (Exception e) {
                    errors.add(new ExcelTraCuuResponse.ExcelRowError(
                        rowData.getRowNumber(), 
                        rowData.getSoVanDon(), 
                        rowData.getSoHieu(), 
                        "Lỗi tra cứu: " + e.getMessage()
                    ));
                    notFoundRows++;
                    log.error("Lỗi khi tra cứu cặp SO_VANDON={}, SO_HIEU={}: ", 
                            rowData.getSoVanDon(), rowData.getSoHieu(), e);
                }
            }
            
            ExcelTraCuuResponse response = new ExcelTraCuuResponse();
            response.setTotalRows(rowDataList.size());
            response.setProcessedRows(rowDataList.size());
            response.setFoundRows(foundRows);
            response.setNotFoundRows(notFoundRows);
            response.setData(foundData);
            response.setErrors(errors);
            
            log.info("Hoàn thành xử lý file Excel: {} dòng, {} tìm thấy, {} không tìm thấy", 
                    rowDataList.size(), foundRows, notFoundRows);
            
            return response;
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý file Excel: ", e);
            throw new BusinessException("Lỗi khi xử lý file Excel: " + e.getMessage());
        }
    }
    
    private List<ExcelRowData> readExcelFile(byte[] fileData) throws BusinessException {
        List<ExcelRowData> results = new ArrayList<>();
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            
            // Tìm header row (dòng chứa tiêu đề)
            int headerRowIndex = findHeaderRow(sheet);
            if (headerRowIndex == -1) {
                throw new BusinessException("Không tìm thấy header row chứa 'Số Vận Đơn' và 'Số Hiệu'");
            }
            
            // Lấy index của các cột Số Vận Đơn và Số Hiệu
            Row headerRow = sheet.getRow(headerRowIndex);
            int soVanDonColumnIndex = findColumnIndex(headerRow, "Số Vận Đơn");
            int soHieuColumnIndex = findColumnIndex(headerRow, "Số Hiệu");
            
            if (soVanDonColumnIndex == -1 && soHieuColumnIndex == -1) {
                throw new BusinessException("Không tìm thấy cột 'Số Vận Đơn' hoặc 'Số Hiệu' trong file Excel");
            }
            
            log.info("Tìm thấy header tại dòng {}, Số Vận Đơn tại cột {}, Số Hiệu tại cột {}", 
                    headerRowIndex + 1, soVanDonColumnIndex + 1, soHieuColumnIndex + 1);
            
            // Đọc dữ liệu từ các dòng sau header
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                String soVanDon = getCellValueAsString(row, soVanDonColumnIndex);
                String soHieu = getCellValueAsString(row, soHieuColumnIndex);
                
                // Bỏ qua dòng trống
                if ((soVanDon == null || soVanDon.trim().isEmpty()) && 
                    (soHieu == null || soHieu.trim().isEmpty())) {
                    continue;
                }
                
                ExcelRowData rowData = new ExcelRowData(
                    i + 1, // Excel row number (1-based)
                    soVanDon,
                    soHieu
                );
                
                results.add(rowData);
            }
            
            log.info("Đọc được {} dòng dữ liệu từ file Excel", results.size());
            
        } catch (IOException e) {
            log.error("Lỗi khi đọc file Excel: ", e);
            throw new BusinessException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
        
        return results;
    }
    
    private int findHeaderRow(Sheet sheet) {
        for (int i = 0; i <= Math.min(10, sheet.getLastRowNum()); i++) { // Tìm trong 10 dòng đầu
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                
                String cellValue = getCellValueAsString(cell);
                if (cellValue != null && 
                    (cellValue.equalsIgnoreCase("Số Vận Đơn") || cellValue.equalsIgnoreCase("Số Hiệu"))) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private int findColumnIndex(Row headerRow, String columnName) {
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) continue;
            
            String cellValue = getCellValueAsString(cell);
            if (cellValue != null && cellValue.equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
    
    private String getCellValueAsString(Row row, int columnIndex) {
        if (columnIndex == -1) return null;
        
        Cell cell = row.getCell(columnIndex);
        return getCellValueAsString(cell);
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Xử lý số nguyên và số thập phân
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
                } else {
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    /**
     * Tạo response cho trường hợp không tìm thấy dữ liệu
     */
    private ToKhaiTraCuuResponse createNotFoundResponse(ExcelRowData rowData) {
        ToKhaiTraCuuResponse response = new ToKhaiTraCuuResponse();
        
        // Chỉ set thông tin từ Excel
        response.setSoVanDon(rowData.getSoVanDon());
        response.setSoHieu(rowData.getSoHieu());
        
        // Set thông báo "Không tìm thấy thông tin trên hệ thống"
        response.setTrangThai("Không tìm thấy thông tin trên hệ thống");
        
        // Các trường khác để null
        response.setToKhaiId(null);
        response.setSoToKhai(null);
        response.setNgayToKhai(null);
        response.setMaDoanhNghiepKhaiPhi(null);
        response.setTenDoanhNghiepKhaiPhi(null);
        response.setMaDoanhNghiepXNK(null);
        response.setTenDoanhNghiepXNK(null);
        response.setSoTiepNhanKhaiPhi(null);
        response.setNgayKhaiPhi(null);
        response.setTongTienPhi(null);
        response.setTrangThaiNganHang(null);
        response.setSoBienLai(null);
        response.setNgayBienLai(null);
        response.setNgayTt(null);
        response.setTransId(null);
        response.setChiTietId(null);
        response.setSoSeal(null);
        response.setLoaiCont(null);
        response.setTinhChatCont(null);
        response.setMaLoaiCont(null);
        response.setMaTcCont(null);
        response.setTongTrongLuong(null);
        response.setDonViTinh(null);
        response.setGhiChu(null);
        response.setDonGia(null);
        response.setSoTien(null);
        
        return response;
    }
}
