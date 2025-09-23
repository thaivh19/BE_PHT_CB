package com.pht.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pht.common.helper.ResponseHelper;
import com.pht.dto.DoiSoatResponse;
import com.pht.dto.DoiSoatSearchRequest;
import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;
import com.pht.exception.BusinessException;
import com.pht.repository.SDoiSoatCtRepository;
import com.pht.service.DoiSoatService;
import com.pht.service.ExcelExportService;
import com.pht.service.NgayLamViecService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/doi-soat")
@Tag(name = "Đối Soát Thủ Công", description = "API chạy đối soát thủ công từ frontend")
public class DoiSoatController {

    @Autowired
    private DoiSoatService doiSoatService;
    
    @Autowired
    private NgayLamViecService ngayLamViecService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    @Autowired
    private SDoiSoatCtRepository sDoiSoatCtRepository;
    
    @Value("${doi-soat.job.cron:0 30 16 * * *}")
    private String cronExpression;

    @PostMapping("/chay-thu-cong")
    @Operation(summary = "Chạy đối soát thủ công", 
               description = "Chạy đối soát thủ công cho ngày cụ thể. Tự động tìm ngày làm việc gần nhất trước ngày được truyền vào.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<?> chayDoiSoatThuCong(
            @Parameter(description = "Ngày đối soát (định dạng: dd/MM/yyyy). Nếu không truyền sẽ lấy ngày hiện tại", 
                      required = false)
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate ngayDoiSoat) {
        
        try {
            // Nếu không truyền ngày, lấy ngày hiện tại
            if (ngayDoiSoat == null) {
                ngayDoiSoat = LocalDate.now();
            }
            
            log.info("API: Chạy đối soát thủ công cho ngày: {}", ngayDoiSoat);
            
            // Kiểm tra ngày đối soát có phải ngày làm việc không
            boolean laNgayLamViec = ngayLamViecService.laNgayLamViec(ngayDoiSoat);
            if (!laNgayLamViec) {
                log.warn("Ngày {} không phải ngày làm việc", ngayDoiSoat);
                String ngayStr = ngayDoiSoat.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String message = "Ngày " + ngayStr + " không phải ngày làm việc. Không thể chạy đối soát.";
                
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("status", "01");
                response.put("message", message);
                response.put("data", null);
                
                return ResponseHelper.ok(response);
            }
            
            // Kiểm tra giờ chạy đối soát cho ngày hiện tại
            LocalDate ngayHienTai = LocalDate.now();
            if (ngayDoiSoat.equals(ngayHienTai)) {
                // Nếu là ngày hiện tại, kiểm tra đã đến giờ chạy đối soát chưa
                String thoiGianChayDoiSoat = layThoiGianChayDoiSoat();
                LocalTime gioChayDoiSoat = LocalTime.parse(thoiGianChayDoiSoat);
                LocalTime gioHienTai = LocalTime.now();
                
                if (gioHienTai.isBefore(gioChayDoiSoat)) {
                    log.warn("Chưa đến giờ chạy đối soát ({}), hiện tại: {}", gioChayDoiSoat, gioHienTai);
                    String message = String.format("Chưa đến giờ chạy đối soát (%s). Hiện tại: %s. Không thể chạy đối soát.", 
                            gioChayDoiSoat, gioHienTai);
                    
                    java.util.Map<String, Object> response = new java.util.HashMap<>();
                    response.put("status", "04");
                    response.put("message", message);
                    response.put("data", null);
                    
                    return ResponseHelper.ok(response);
                }
            } else if (ngayDoiSoat.isAfter(ngayHienTai)) {
                // Nếu là ngày tương lai, không được phép tổng hợp
                log.warn("Ngày đối soát {} là ngày tương lai, không được phép tổng hợp", ngayDoiSoat);
                String ngayStr = ngayDoiSoat.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String message = "Ngày " + ngayStr + " là ngày tương lai. Không được phép tổng hợp đối soát.";
                
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("status", "05");
                response.put("message", message);
                response.put("data", null);
                
                return ResponseHelper.ok(response);
            }
            
            // Tìm ngày làm việc gần nhất trước ngày đối soát
            LocalDate ngayLamViecGanNhat = ngayLamViecService.timNgayLamViecGanNhat(ngayDoiSoat);
            if (ngayLamViecGanNhat == null) {
                log.warn("Không tìm thấy ngày làm việc gần nhất trước ngày {}", ngayDoiSoat);
                String ngayStr = ngayDoiSoat.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String message = "Không tìm thấy ngày làm việc gần nhất trước ngày " + ngayStr + ". Không thể chạy đối soát.";
                
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("status", "02");
                response.put("message", message);
                response.put("data", null);
                
                return ResponseHelper.ok(response);
            }
            
            log.info("Tìm thấy ngày làm việc gần nhất: {}", ngayLamViecGanNhat);
            
            // Chạy đối soát thủ công từ ngày làm việc gần nhất đến ngày đối soát
            SDoiSoat doiSoat = doiSoatService.chayDoiSoatThuCong(ngayLamViecGanNhat, ngayDoiSoat);
            
            if (doiSoat == null) {
                String ngayBatDauStr = ngayLamViecGanNhat.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String ngayKetThucStr = ngayDoiSoat.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String message = "Không có tờ khai nào để đối soát từ " + ngayBatDauStr + " đến " + ngayKetThucStr;
                
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("status", "03");
                response.put("message", message);
                response.put("data", null);
                
                return ResponseHelper.ok(response);
            }
            
            log.info("Hoàn thành đối soát thủ công cho ngày: {} với ID: {}", ngayDoiSoat, doiSoat.getId());
            
            // Tạo response thành công với status "00"
            String message = String.format("Đối soát thành công cho ngày %s. Tổng số tờ khai: %d, Tổng tiền: %s", 
                    ngayDoiSoat.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    doiSoat.getTongSo(),
                    doiSoat.getTongTien());
            
            // Tạo response object với status "00"
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("status", "00");
            response.put("message", message);
            response.put("data", doiSoat);
            
            return ResponseHelper.ok(response);
            
        } catch (Exception e) {
            log.error("Lỗi khi chạy đối soát thủ công cho ngày {}: ", ngayDoiSoat, e);
            return ResponseHelper.error(e);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Lấy danh sách tất cả đối soát", 
               description = "Lấy danh sách tất cả các bản ghi đối soát trong hệ thống")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<?> getAllDoiSoat() {
        try {
            log.info("API: Lấy danh sách tất cả đối soát");
            List<SDoiSoat> result = doiSoatService.getAll();
            log.info("Lấy thành công {} bản ghi đối soát", result.size());
            
            // Convert sang DoiSoatResponse để format ngày và tiền
            List<DoiSoatResponse> responseList = result.stream()
                    .map(DoiSoatResponse::new)
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseHelper.ok(responseList);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách đối soát: ", e);
            return ResponseHelper.error(e);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin đối soát theo ID", 
               description = "Lấy thông tin chi tiết của một bản ghi đối soát theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy đối soát"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<?> getDoiSoatById(
            @Parameter(description = "ID của đối soát cần lấy", required = true)
            @PathVariable Long id) {
        try {
            log.info("API: Lấy thông tin đối soát với ID: {}", id);
            SDoiSoat result = doiSoatService.getById(id);
            log.info("Lấy thành công đối soát với ID: {}", id);
            
            // Convert sang DoiSoatResponse để format ngày và tiền
            DoiSoatResponse response = new DoiSoatResponse(result);
            
            return ResponseHelper.ok(response);
        } catch (BusinessException e) {
            log.warn("Không tìm thấy đối soát với ID: {}", id);
            return ResponseHelper.notFound(null, e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi khi lấy đối soát với ID {}: ", id, e);
            return ResponseHelper.error(e);
        }
    }
    
    /**
     * Lấy thời gian chạy đối soát từ cron expression
     * Cron format: giây phút giờ ngày tháng năm
     * Ví dụ: "0 30 16 * * *" -> 16:30
     */
    private String layThoiGianChayDoiSoat() {
        try {
            log.info("Cron expression: {}", cronExpression);
            
            // Parse cron expression: giây phút giờ ngày tháng năm
            String[] parts = cronExpression.trim().split("\\s+");
            if (parts.length >= 3) {
                String phut = parts[1];  // phút
                String gio = parts[2];   // giờ
                
                // Tạo thời gian theo format HH:mm
                String thoiGianChayDoiSoat = String.format("%02d:%02d", Integer.parseInt(gio), Integer.parseInt(phut));
                
                log.info("Thời gian chạy đối soát từ cron: {}", thoiGianChayDoiSoat);
                return thoiGianChayDoiSoat;
            } else {
                log.warn("Cron expression không đúng định dạng: {}, sử dụng giá trị mặc định 16:30", cronExpression);
                return "16:30";
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi parse cron expression: {}, sử dụng giá trị mặc định 16:30", cronExpression, e);
            return "16:30";
        }
    }
    
    /**
     * Lấy thời gian COT từ bảng ngày làm việc
     */
    @SuppressWarnings("unused")
    private String layThoiGianCot(LocalDate ngay) {
        try {
            String cot = ngayLamViecService.layCotTuNgayLamViec(ngay);
            log.info("COT từ ngày làm việc {}: {}", ngay, cot);
            return cot;
        } catch (Exception e) {
            log.error("Lỗi khi lấy COT từ ngày làm việc {}: ", ngay, e);
            return "16:00";
        }
    }

    // ========== SEARCH ==========

    @Operation(summary = "Tìm kiếm đối soát từ ngày đến ngày", 
               description = "Tìm kiếm đối soát theo khoảng thời gian và các filter tùy chọn")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchDoiSoat(@RequestBody DoiSoatSearchRequest request) {
        try {
            log.info("Nhận yêu cầu tìm kiếm đối soát từ {} đến {}", request.getTuNgay(), request.getDenNgay());
            
            List<SDoiSoat> result = doiSoatService.searchDoiSoat(request);
            
            // Convert sang DoiSoatResponse để format ngày và tiền
            List<DoiSoatResponse> responseList = result.stream()
                    .map(DoiSoatResponse::new)
                    .collect(java.util.stream.Collectors.toList());
            
            log.info("Tìm kiếm thành công {} bản ghi đối soát", result.size());
            
            return ResponseHelper.ok(responseList);
            
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tìm kiếm đối soát: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tìm kiếm đối soát: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== EXPORT ==========

    @Operation(summary = "Xuất dữ liệu đối soát với layout master-detail", 
               description = "Xuất dữ liệu đối soát từ ngày đến ngày với layout master-detail trong 1 sheet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @PostMapping("/export-master-detail")
    public ResponseEntity<?> exportDoiSoatMasterDetail(@RequestBody DoiSoatSearchRequest request) {
        try {
            log.info("Nhận yêu cầu export đối soát master-detail từ {} đến {}", request.getTuNgay(), request.getDenNgay());
            
            // Tìm kiếm dữ liệu đối soát theo điều kiện
            List<SDoiSoat> doiSoatList = doiSoatService.searchDoiSoat(request);
            
            if (doiSoatList.isEmpty()) {
                throw new BusinessException("Không có dữ liệu để export");
            }
            
            // Tạo file Excel với layout master-detail
            String fileName = String.format("DoiSoat_MasterDetail_%s_%s_%s.xlsx", 
                    request.getTuNgay().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                    request.getDenNgay().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                    System.currentTimeMillis());
            
            byte[] excelData = excelExportService.exportDoiSoatMasterDetailToExcel(doiSoatList, fileName);
            
            log.info("Export thành công {} đối soát ra Excel với layout master-detail", doiSoatList.size());
            
            // Tạo response với file Excel
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
            
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi export đối soát master-detail: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi export đối soát master-detail: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xuất dữ liệu đối soát với nhiều sheet", 
               description = "Xuất dữ liệu đối soát từ ngày đến ngày với nhiều sheet (tổng quan và chi tiết)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @PostMapping("/export")
    public ResponseEntity<?> exportDoiSoatByDate(@RequestBody DoiSoatSearchRequest request) {
        try {
            log.info("Nhận yêu cầu export đối soát từ {} đến {}", request.getTuNgay(), request.getDenNgay());
            
            // Sử dụng API search để lấy dữ liệu đối soát tổng quan
            List<SDoiSoat> doiSoatList = doiSoatService.searchDoiSoat(request);
            
            if (doiSoatList.isEmpty()) {
                throw new BusinessException("Không có dữ liệu để export");
            }
            
            // Lấy dữ liệu đối soát chi tiết cho tất cả các đối soát tìm được
            List<SDoiSoatCt> doiSoatCtList = new java.util.ArrayList<>();
            for (SDoiSoat doiSoat : doiSoatList) {
                // Lấy chi tiết cho từng đối soát
                List<SDoiSoatCt> chiTietList = sDoiSoatCtRepository.findByDoiSoatId(doiSoat.getId());
                doiSoatCtList.addAll(chiTietList);
            }
            
            // Tạo file Excel với nhiều sheet
            String fileName = String.format("DoiSoat_%s_%s_%s.xlsx", 
                    request.getTuNgay().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                    request.getDenNgay().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                    System.currentTimeMillis());
            
            byte[] excelData = excelExportService.exportDoiSoatToExcel(doiSoatList, doiSoatCtList, fileName);
            
            log.info("Export thành công {} đối soát và {} chi tiết đối soát ra Excel", 
                    doiSoatList.size(), doiSoatCtList.size());
            
            // Tạo response với file Excel
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
            
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi export đối soát: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi export đối soát: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}
