package com.pht.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pht.dto.ExcelTraCuuResponse;
import com.pht.dto.ToKhaiTraCuuResponse;
import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;
import com.pht.exception.BusinessException;
import com.pht.repository.SDoiSoatCtRepository;
import com.pht.service.ExcelExportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Autowired
    private SDoiSoatCtRepository sDoiSoatCtRepository;

    @Override
    public byte[] exportToKhaiTraCuuToExcel(List<ToKhaiTraCuuResponse> data, String fileName) throws BusinessException {
        log.info("Bắt đầu export {} kết quả tra cứu tờ khai ra Excel", data.size());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tra cứu tờ khai");
            
            // Tạo style cho header
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Tạo header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "STT", "Số tờ khai", "Ngày tờ khai", "Mã DN khai phí", "Tên DN khai phí",
                "Mã DN XNK", "Tên DN XNK", "Số tiếp nhận KP", "Ngày khai phí", "Tổng tiền phí",
                "Trạng thái NH", "Trạng thái xuất cảng", "Số biên lai", "Ngày biên lai", "Ngày TT", "Trạng thái", "Trans ID",
                "Số vận đơn", "Số hiệu", "Số seal", "Loại cont", "Tính chất cont", "Mã loại cont",
                "Mã TC cont", "Tổng trọng lượng", "Đơn vị tính", "Ghi chú", "Đơn giá", "Số tiền"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Tạo data rows
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            for (int i = 0; i < data.size(); i++) {
                ToKhaiTraCuuResponse item = data.get(i);
                Row row = sheet.createRow(i + 1);
                
                int colIndex = 0;
                row.createCell(colIndex++).setCellValue(i + 1); // STT
                row.createCell(colIndex++).setCellValue(item.getSoToKhai() != null ? item.getSoToKhai() : "");
                row.createCell(colIndex++).setCellValue(item.getNgayToKhai() != null ? item.getNgayToKhai().format(dateFormatter) : "");
                row.createCell(colIndex++).setCellValue(item.getMaDoanhNghiepKhaiPhi() != null ? item.getMaDoanhNghiepKhaiPhi() : "");
                row.createCell(colIndex++).setCellValue(item.getTenDoanhNghiepKhaiPhi() != null ? item.getTenDoanhNghiepKhaiPhi() : "");
                row.createCell(colIndex++).setCellValue(item.getMaDoanhNghiepXNK() != null ? item.getMaDoanhNghiepXNK() : "");
                row.createCell(colIndex++).setCellValue(item.getTenDoanhNghiepXNK() != null ? item.getTenDoanhNghiepXNK() : "");
                row.createCell(colIndex++).setCellValue(item.getSoTiepNhanKhaiPhi() != null ? item.getSoTiepNhanKhaiPhi() : "");
                row.createCell(colIndex++).setCellValue(item.getNgayKhaiPhi() != null ? item.getNgayKhaiPhi().format(dateFormatter) : "");
                row.createCell(colIndex++).setCellValue(item.getTongTienPhi() != null ? item.getTongTienPhi().toString() : "");
                row.createCell(colIndex++).setCellValue(item.getTrangThaiNganHang() != null ? item.getTrangThaiNganHang() : "");
                row.createCell(colIndex++).setCellValue(getTrangThaiXuatCang(item.getTrangThaiNganHang()));
                row.createCell(colIndex++).setCellValue(item.getSoBienLai() != null ? item.getSoBienLai() : "");
                row.createCell(colIndex++).setCellValue(item.getNgayBienLai() != null ? item.getNgayBienLai().format(dateFormatter) : "");
                row.createCell(colIndex++).setCellValue(item.getNgayTt() != null ? item.getNgayTt().format(dateTimeFormatter) : "");
                row.createCell(colIndex++).setCellValue(item.getTrangThai() != null ? item.getTrangThai() : "");
                row.createCell(colIndex++).setCellValue(item.getTransId() != null ? item.getTransId() : "");
                row.createCell(colIndex++).setCellValue(item.getSoVanDon() != null ? item.getSoVanDon() : "");
                row.createCell(colIndex++).setCellValue(item.getSoHieu() != null ? item.getSoHieu() : "");
                row.createCell(colIndex++).setCellValue(item.getSoSeal() != null ? item.getSoSeal() : "");
                row.createCell(colIndex++).setCellValue(item.getLoaiCont() != null ? item.getLoaiCont() : "");
                row.createCell(colIndex++).setCellValue(item.getTinhChatCont() != null ? item.getTinhChatCont() : "");
                row.createCell(colIndex++).setCellValue(item.getMaLoaiCont() != null ? item.getMaLoaiCont() : "");
                row.createCell(colIndex++).setCellValue(item.getMaTcCont() != null ? item.getMaTcCont() : "");
                row.createCell(colIndex++).setCellValue(item.getTongTrongLuong() != null ? item.getTongTrongLuong().toString() : "");
                row.createCell(colIndex++).setCellValue(item.getDonViTinh() != null ? item.getDonViTinh() : "");
                row.createCell(colIndex++).setCellValue(item.getGhiChu() != null ? item.getGhiChu() : "");
                row.createCell(colIndex++).setCellValue(item.getDonGia() != null ? item.getDonGia().toString() : "");
                row.createCell(colIndex++).setCellValue(item.getSoTien() != null ? item.getSoTien().toString() : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            log.info("Export thành công {} kết quả tra cứu tờ khai ra Excel", data.size());
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("Lỗi khi export Excel: ", e);
            throw new BusinessException("Lỗi khi export Excel: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportExcelTraCuuToExcel(ExcelTraCuuResponse data, String fileName) throws BusinessException {
        log.info("Bắt đầu export kết quả Excel tra cứu ra Excel: {} dòng, {} tìm thấy, {} lỗi", 
                data.getTotalRows(), data.getFoundRows(), data.getNotFoundRows());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Sheet 1: Kết quả tìm thấy
            Sheet successSheet = workbook.createSheet("Kết quả tìm thấy");
            createSuccessSheet(successSheet, data.getData(), workbook);
            
            // Sheet 2: Lỗi - Không tìm thấy
            Sheet errorSheet = workbook.createSheet("Lỗi - Không tìm thấy");
            createErrorSheet(errorSheet, data.getErrors(), workbook);
            
            // Sheet 3: Thống kê
            Sheet summarySheet = workbook.createSheet("Thống kê");
            createSummarySheet(summarySheet, data, workbook);
            
            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            log.info("Export thành công kết quả Excel tra cứu ra Excel");
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("Lỗi khi export Excel: ", e);
            throw new BusinessException("Lỗi khi export Excel: " + e.getMessage());
        }
    }
    
    private void createSuccessSheet(Sheet sheet, List<ToKhaiTraCuuResponse> data, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // Header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "STT", "Số tờ khai", "Ngày tờ khai", "Mã DN khai phí", "Tên DN khai phí",
            "Số vận đơn", "Số hiệu", "Tổng tiền phí", "Trạng thái NH", "Trạng thái xuất cảng", "Trạng thái", "Ngày TT"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data rows
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        for (int i = 0; i < data.size(); i++) {
            ToKhaiTraCuuResponse item = data.get(i);
            Row row = sheet.createRow(i + 1);
            
            int colIndex = 0;
            row.createCell(colIndex++).setCellValue(i + 1); // STT
            row.createCell(colIndex++).setCellValue(item.getSoToKhai() != null ? item.getSoToKhai() : "");
            row.createCell(colIndex++).setCellValue(item.getNgayToKhai() != null ? item.getNgayToKhai().format(dateFormatter) : "");
            row.createCell(colIndex++).setCellValue(item.getMaDoanhNghiepKhaiPhi() != null ? item.getMaDoanhNghiepKhaiPhi() : "");
            row.createCell(colIndex++).setCellValue(item.getTenDoanhNghiepKhaiPhi() != null ? item.getTenDoanhNghiepKhaiPhi() : "");
            row.createCell(colIndex++).setCellValue(item.getSoVanDon() != null ? item.getSoVanDon() : "");
            row.createCell(colIndex++).setCellValue(item.getSoHieu() != null ? item.getSoHieu() : "");
            row.createCell(colIndex++).setCellValue(item.getTongTienPhi() != null ? item.getTongTienPhi().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getTrangThaiNganHang() != null ? item.getTrangThaiNganHang() : "");
            row.createCell(colIndex++).setCellValue(getTrangThaiXuatCang(item.getTrangThaiNganHang()));
            row.createCell(colIndex++).setCellValue(item.getTrangThai() != null ? item.getTrangThai() : "");
            row.createCell(colIndex++).setCellValue(item.getNgayTt() != null ? item.getNgayTt().format(dateTimeFormatter) : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createErrorSheet(Sheet sheet, List<ExcelTraCuuResponse.ExcelRowError> errors, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // Header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"STT", "Dòng Excel", "SO_VANDON", "SO_HIEU", "Lỗi"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data rows
        for (int i = 0; i < errors.size(); i++) {
            ExcelTraCuuResponse.ExcelRowError error = errors.get(i);
            Row row = sheet.createRow(i + 1);
            
            int colIndex = 0;
            row.createCell(colIndex++).setCellValue(i + 1); // STT
            row.createCell(colIndex++).setCellValue(error.getRowNumber());
            row.createCell(colIndex++).setCellValue(error.getSoVanDon() != null ? error.getSoVanDon() : "");
            row.createCell(colIndex++).setCellValue(error.getSoHieu() != null ? error.getSoHieu() : "");
            row.createCell(colIndex++).setCellValue(error.getErrorMessage() != null ? error.getErrorMessage() : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createSummarySheet(Sheet sheet, ExcelTraCuuResponse data, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        
        // Header
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("THỐNG KÊ KẾT QUẢ TRA CỨU");
        headerCell.setCellStyle(headerStyle);
        
        // Data
        int rowIndex = 2;
        String[][] summaryData = {
            {"Tổng số dòng trong Excel", String.valueOf(data.getTotalRows())},
            {"Số dòng đã xử lý", String.valueOf(data.getProcessedRows())},
            {"Số dòng tìm thấy kết quả", String.valueOf(data.getFoundRows())},
            {"Số dòng không tìm thấy", String.valueOf(data.getNotFoundRows())},
            {"Tỷ lệ thành công", String.format("%.2f%%", (double) data.getFoundRows() / data.getTotalRows() * 100)}
        };
        
        for (String[] rowData : summaryData) {
            Row row = sheet.createRow(rowIndex++);
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(rowData[0]);
            labelCell.setCellStyle(dataStyle);
            
            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(rowData[1]);
            valueCell.setCellStyle(dataStyle);
        }
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    /**
     * Xác định trạng thái xuất cảng dựa trên trạng thái ngân hàng
     * @param trangThaiNganHang Trạng thái ngân hàng
     * @return "Chưa xuất cảng" nếu là 00 hoặc 01, "Đã xuất cảng" nếu khác
     */
    private String getTrangThaiXuatCang(String trangThaiNganHang) {
        if (trangThaiNganHang == null || trangThaiNganHang.trim().isEmpty()) {
            return "";
        }
        
        String trangThai = trangThaiNganHang.trim();
        if ("00".equals(trangThai) || "01".equals(trangThai)) {
            return "Chưa xuất cảng";
        } else {
            return "Đã xuất cảng";
        }
    }
    
    /**
     * Chuyển đổi mã trạng thái đối soát ngân hàng thành text tiếng Việt
     * @param nhDs Mã trạng thái đối soát ngân hàng
     * @return Text mô tả trạng thái
     */
    private String getNhDsStatusText(String nhDs) {
        if (nhDs == null || nhDs.trim().isEmpty()) {
            return "";
        }
        
        String status = nhDs.trim();
        switch (status) {
            case "00":
                return "Chưa đối soát";
            case "01":
                return "Khớp";
            case "02":
            case "03":
                return "Lệch";
            default:
                return status; // Trả về mã gốc nếu không nhận diện được
        }
    }
    
    /**
     * Chuyển đổi mã trạng thái đối soát kho bạc thành text tiếng Việt
     * @param kbDs Mã trạng thái đối soát kho bạc
     * @return Text mô tả trạng thái
     */
    private String getKbDsStatusText(String kbDs) {
        if (kbDs == null || kbDs.trim().isEmpty()) {
            return "";
        }
        
        String status = kbDs.trim();
        switch (status) {
            case "00":
                return "Chưa đối soát";
            case "01":
                return "Khớp";
            case "02":
            case "03":
                return "Lệch";
            default:
                return status; // Trả về mã gốc nếu không nhận diện được
        }
    }

    @Override
    public byte[] exportDoiSoatToExcel(List<SDoiSoat> doiSoatList, List<SDoiSoatCt> doiSoatCtList, String fileName) throws BusinessException {
        log.info("Bắt đầu export {} đối soát và {} chi tiết đối soát ra Excel", doiSoatList.size(), doiSoatCtList.size());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Tạo style cho header
            CellStyle headerStyle = createHeaderStyle(workbook);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            // Sheet 1: Tổng quan đối soát
            createDoiSoatSummarySheet(workbook, doiSoatList, headerStyle, dateFormatter);
            
            // Sheet 2: Chi tiết đối soát
            createDoiSoatDetailSheet(workbook, doiSoatCtList, headerStyle, dateFormatter);
            
            // Convert workbook to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            log.info("Export thành công file Excel: {}", fileName);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("Lỗi khi tạo file Excel: ", e);
            throw new BusinessException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }
    
    /**
     * Tạo sheet tổng quan đối soát
     */
    private void createDoiSoatSummarySheet(Workbook workbook, List<SDoiSoat> doiSoatList, CellStyle headerStyle, DateTimeFormatter dateFormatter) {
        Sheet sheet = workbook.createSheet("Tổng quan đối soát");
        
        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "STT", "ID", "Số BK", "Ngày BK", "Ngày DS", "Lần DS", "Trạng thái", 
            "Tổng số", "Tổng tiền", "NH DS", "KB DS", "TS TK DDS NH", "TS TK DDS KB",
            "TS TK THUA NH", "TS TK THUA KB", "Ghi chú NH", "Ghi chú KB"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Tạo data rows
        for (int i = 0; i < doiSoatList.size(); i++) {
            SDoiSoat item = doiSoatList.get(i);
            Row row = sheet.createRow(i + 1);
            
            int colIndex = 0;
            row.createCell(colIndex++).setCellValue(i + 1); // STT
            row.createCell(colIndex++).setCellValue(item.getId() != null ? item.getId().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getSoBk() != null ? item.getSoBk() : "");
            row.createCell(colIndex++).setCellValue(item.getNgayBk() != null ? item.getNgayBk().format(dateFormatter) : "");
            row.createCell(colIndex++).setCellValue(item.getNgayDs() != null ? item.getNgayDs().format(dateFormatter) : "");
            row.createCell(colIndex++).setCellValue(item.getLanDs() != null ? item.getLanDs().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getTrangThai() != null ? item.getTrangThai() : "");
            row.createCell(colIndex++).setCellValue(item.getTongSo() != null ? item.getTongSo().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getTongTien() != null ? item.getTongTien().toString() : "");
            row.createCell(colIndex++).setCellValue(getNhDsStatusText(item.getNhDs()));
            row.createCell(colIndex++).setCellValue(getKbDsStatusText(item.getKbDs()));
            row.createCell(colIndex++).setCellValue(item.getTongSoTkDdsNh() != null ? item.getTongSoTkDdsNh().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getTongSoTkDdsKb() != null ? item.getTongSoTkDdsKb().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getTongSoTkThuaNh() != null ? item.getTongSoTkThuaNh().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getTongSoTkThuaKb() != null ? item.getTongSoTkThuaKb().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getGhiChuNh() != null ? item.getGhiChuNh() : "");
            row.createCell(colIndex++).setCellValue(item.getGhiChuKb() != null ? item.getGhiChuKb() : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Tạo sheet chi tiết đối soát
     */
    private void createDoiSoatDetailSheet(Workbook workbook, List<SDoiSoatCt> doiSoatCtList, CellStyle headerStyle, DateTimeFormatter dateFormatter) {
        Sheet sheet = workbook.createSheet("Chi tiết đối soát");
        
        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "STT", "ID", "DOI SOAT ID", "STO KHAI ID", "Số tờ khai", "Ngày tờ khai", 
            "Số TN KP", "Ngày TN KP", "Mã DN", "Tên DN", "Tổng tiền phí", "Trans ID",
            "Ngân hàng", "NH DS", "KB DS", "Ghi chú"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Tạo data rows
        for (int i = 0; i < doiSoatCtList.size(); i++) {
            SDoiSoatCt item = doiSoatCtList.get(i);
            Row row = sheet.createRow(i + 1);
            
            int colIndex = 0;
            row.createCell(colIndex++).setCellValue(i + 1); // STT
            row.createCell(colIndex++).setCellValue(item.getId() != null ? item.getId().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getDoiSoatId() != null ? item.getDoiSoatId().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getStoKhaiId() != null ? item.getStoKhaiId().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getSoToKhai() != null ? item.getSoToKhai() : "");
            row.createCell(colIndex++).setCellValue(item.getNgayToKhai() != null ? item.getNgayToKhai().format(dateFormatter) : "");
            row.createCell(colIndex++).setCellValue(item.getSoTnKp() != null ? item.getSoTnKp() : "");
            row.createCell(colIndex++).setCellValue(item.getNgayTnKp() != null ? item.getNgayTnKp().format(dateFormatter) : "");
            row.createCell(colIndex++).setCellValue(item.getMaDoanhNghiep() != null ? item.getMaDoanhNghiep() : "");
            row.createCell(colIndex++).setCellValue(item.getTenDoanhNghiep() != null ? item.getTenDoanhNghiep() : "");
            row.createCell(colIndex++).setCellValue(item.getTongTienPhi() != null ? item.getTongTienPhi().toString() : "");
            row.createCell(colIndex++).setCellValue(item.getTransId() != null ? item.getTransId() : "");
            row.createCell(colIndex++).setCellValue(item.getNganHang() != null ? item.getNganHang() : "");
            row.createCell(colIndex++).setCellValue(getNhDsStatusText(item.getNhDs()));
            row.createCell(colIndex++).setCellValue(getKbDsStatusText(item.getKbDs()));
            row.createCell(colIndex++).setCellValue(item.getGhiChu() != null ? item.getGhiChu() : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    @Override
    public byte[] exportDoiSoatMasterDetailToExcel(List<SDoiSoat> doiSoatList, String fileName) throws BusinessException {
        log.info("Bắt đầu export {} đối soát ra Excel với layout master-detail", doiSoatList.size());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Đối soát chi tiết");
            
            // Tạo style cho header và master row
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle masterStyle = createMasterRowStyle(workbook);
            CellStyle detailStyle = createDetailRowStyle(workbook);
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            int currentRow = 0;
            
            // Tạo header row cho toàn bộ sheet
            Row headerRow = sheet.createRow(currentRow++);
            String[] headers = {
                "Loại", "Số BK/TK", "Ngày BK/TK", "Ngày đối soát", "Lần đối soát",
                "Mã doanh nghiệp", "Tên doanh nghiệp", "Tổng số/tiền", "Đối soát với NH", "Đối soát với KB", "Trans ID", "Ngân hàng", "Ghi chú"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Lặp qua từng SDoiSoat và tạo master-detail rows
            for (SDoiSoat doiSoat : doiSoatList) {
                // Tạo master row (thông tin đối soát chính)
                Row masterRow = sheet.createRow(currentRow++);
                masterRow.setRowStyle(masterStyle);
                
                int colIndex = 0;
                masterRow.createCell(colIndex++).setCellValue("Thông tin");
               // masterRow.createCell(colIndex++).setCellValue(doiSoat.getId() != null ? doiSoat.getId().toString() : "");
                masterRow.createCell(colIndex++).setCellValue(doiSoat.getSoBk() != null ? doiSoat.getSoBk() : "");
                masterRow.createCell(colIndex++).setCellValue(doiSoat.getNgayBk() != null ? doiSoat.getNgayBk().format(dateFormatter) : "");
                masterRow.createCell(colIndex++).setCellValue(doiSoat.getNgayDs() != null ? doiSoat.getNgayDs().format(dateFormatter) : "");
                masterRow.createCell(colIndex++).setCellValue(doiSoat.getLanDs() != null ? doiSoat.getLanDs().toString() : "");
                //masterRow.createCell(colIndex++).setCellValue(doiSoat.getTrangThai() != null ? doiSoat.getTrangThai() : "");
                masterRow.createCell(colIndex++).setCellValue(""); // Mã DN - không có trong master
                masterRow.createCell(colIndex++).setCellValue(""); // Tên DN - không có trong master
                masterRow.createCell(colIndex++).setCellValue(String.format("Tổng: %s tờ khai - %s VND", 
                    doiSoat.getTongSo() != null ? doiSoat.getTongSo() : 0,
                    doiSoat.getTongTien() != null ? doiSoat.getTongTien() : 0));
                masterRow.createCell(colIndex++).setCellValue(getNhDsStatusText(doiSoat.getNhDs()));
                masterRow.createCell(colIndex++).setCellValue(getKbDsStatusText(doiSoat.getKbDs()));
                masterRow.createCell(colIndex++).setCellValue(""); // Trans ID - không có trong master
                masterRow.createCell(colIndex++).setCellValue(""); // Ngân hàng - không có trong master
                masterRow.createCell(colIndex++).setCellValue(String.format("NH: %s | KB: %s", 
                    doiSoat.getGhiChuNh() != null ? doiSoat.getGhiChuNh() : "",
                    doiSoat.getGhiChuKb() != null ? doiSoat.getGhiChuKb() : ""));
                
                // Lấy chi tiết đối soát cho SDoiSoat này
                List<SDoiSoatCt> chiTietList = getChiTietByDoiSoatId(doiSoat.getId());
                
                // Tạo detail rows (thông tin chi tiết)
                for (SDoiSoatCt chiTiet : chiTietList) {
                    Row detailRow = sheet.createRow(currentRow++);
                    detailRow.setRowStyle(detailStyle);
                    
                    colIndex = 0;
                    detailRow.createCell(colIndex++).setCellValue("Thông tin chi tiết");
                    //detailRow.createCell(colIndex++).setCellValue(chiTiet.getId() != null ? chiTiet.getId().toString() : "");
                    detailRow.createCell(colIndex++).setCellValue(chiTiet.getSoToKhai() != null ? chiTiet.getSoToKhai() : "");
                    detailRow.createCell(colIndex++).setCellValue(chiTiet.getNgayToKhai() != null ? chiTiet.getNgayToKhai().format(dateFormatter) : "");
                    detailRow.createCell(colIndex++).setCellValue(""); // Ngày DS - duplicate từ master
                    detailRow.createCell(colIndex++).setCellValue(""); // Lần DS - duplicate từ master
                    //detailRow.createCell(colIndex++).setCellValue(""); // Trạng thái - duplicate từ master
                    detailRow.createCell(colIndex++).setCellValue(chiTiet.getMaDoanhNghiep() != null ? chiTiet.getMaDoanhNghiep() : "");
                    detailRow.createCell(colIndex++).setCellValue(chiTiet.getTenDoanhNghiep() != null ? chiTiet.getTenDoanhNghiep() : "");
                    detailRow.createCell(colIndex++).setCellValue(chiTiet.getTongTienPhi() != null ? chiTiet.getTongTienPhi().toString() : "");
                    detailRow.createCell(colIndex++).setCellValue(getNhDsStatusText(chiTiet.getNhDs()));
                    detailRow.createCell(colIndex++).setCellValue(getKbDsStatusText(chiTiet.getKbDs()));
                    detailRow.createCell(colIndex++).setCellValue(chiTiet.getTransId() != null ? chiTiet.getTransId() : "");
                    detailRow.createCell(colIndex++).setCellValue(chiTiet.getNganHang() != null ? chiTiet.getNganHang() : "");
                    detailRow.createCell(colIndex++).setCellValue(chiTiet.getGhiChu() != null ? chiTiet.getGhiChu() : "");
                }
                
                // Thêm một dòng trống để phân cách giữa các đối soát
                currentRow++;
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convert workbook to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            log.info("Export thành công file Excel master-detail: {}", fileName);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("Lỗi khi tạo file Excel: ", e);
            throw new BusinessException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }
    
    /**
     * Tạo style cho master row
     */
    private CellStyle createMasterRowStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(org.apache.poi.ss.usermodel.IndexedColors.BLUE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    
    /**
     * Tạo style cho detail row
     */
    private CellStyle createDetailRowStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    
    /**
     * Lấy chi tiết đối soát theo DOI_SOAT_ID
     */
    private List<SDoiSoatCt> getChiTietByDoiSoatId(Long doiSoatId) {
        try {
            return sDoiSoatCtRepository.findByDoiSoatId(doiSoatId);
        } catch (Exception e) {
            log.error("Lỗi khi lấy chi tiết đối soát cho DOI_SOAT_ID {}: ", doiSoatId, e);
            return new java.util.ArrayList<>();
        }
    }
}
