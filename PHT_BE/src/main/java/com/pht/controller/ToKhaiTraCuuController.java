package com.pht.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pht.common.helper.ResponseHelper;
import com.pht.dto.ExcelTraCuuResponse;
import com.pht.dto.ToKhaiTraCuuResponse;
import com.pht.exception.BusinessException;
import com.pht.service.ExcelExportService;
import com.pht.service.ExcelTraCuuService;
import com.pht.service.ToKhaiTraCuuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/get-in-get-out")
@Tag(name = "Tra cứu tờ khai GI GO", description = "API get in get out")
public class ToKhaiTraCuuController {

    private final ToKhaiTraCuuService toKhaiTraCuuService;
    private final ExcelTraCuuService excelTraCuuService;
    private final ExcelExportService excelExportService;

    @Operation(summary = "Tra cứu tờ khai theo SO_VANDON và SO_HIEU", 
               description = "Tra cứu thông tin tờ khai và chi tiết tờ khai. " +
                           "Nếu một trong hai tham số null thì tra cứu theo thông tin có dữ liệu. " +
                           "Nếu cả hai đều có dữ liệu thì tra cứu theo cả hai.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ToKhaiTraCuuResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/search")
    public ResponseEntity<?> traCuuToKhai(
            @Parameter(description = "Số vận đơn", example = "VD001", required = false)
            @RequestParam(required = false) String soVanDon,
            
            @Parameter(description = "Số hiệu", example = "SH001", required = false)
            @RequestParam(required = false) String soHieu) {
        
        try {
            log.info("Nhận yêu cầu tra cứu tờ khai - SO_VANDON: {}, SO_HIEU: {}", soVanDon, soHieu);
            
            List<ToKhaiTraCuuResponse> result = toKhaiTraCuuService.traCuuToKhai(soVanDon, soHieu);
            
            log.info("Tra cứu thành công, tìm thấy {} kết quả", result.size());
            
            return ResponseHelper.ok(result);
            
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tra cứu tờ khai: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tra cứu tờ khai: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Export kết quả tra cứu tờ khai ra Excel", 
               description = "Export kết quả tra cứu tờ khai theo SO_VANDON và SO_HIEU ra file Excel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = byte[].class), mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/export-excel")
    public ResponseEntity<?> exportToKhaiTraCuuToExcel(
            @Parameter(description = "Số vận đơn", example = "VD001", required = false)
            @RequestParam(required = false) String soVanDon,
            
            @Parameter(description = "Số hiệu", example = "SH001", required = false)
            @RequestParam(required = false) String soHieu) {
        
        try {
            log.info("Nhận yêu cầu export Excel tra cứu tờ khai - SO_VANDON: {}, SO_HIEU: {}", soVanDon, soHieu);
            
            // Tra cứu dữ liệu
            List<ToKhaiTraCuuResponse> data = toKhaiTraCuuService.traCuuToKhai(soVanDon, soHieu);
            
            if (data.isEmpty()) {
                throw new BusinessException("Không có dữ liệu để export");
            }
            
            // Export ra Excel
            String fileName = String.format("TraCuuToKhai_%s_%s.xlsx", 
                    System.currentTimeMillis(),
                    soVanDon != null ? soVanDon : soHieu != null ? soHieu : "All");
            
            byte[] excelData = excelExportService.exportToKhaiTraCuuToExcel(data, fileName);
            
            log.info("Export thành công {} kết quả tra cứu tờ khai ra Excel", data.size());
            
            // Tạo response với file Excel
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
            
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi export Excel: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi export Excel: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Upload file Excel và tra cứu tờ khai", 
               description = "Upload file Excel chứa cột 'Số Vận Đơn' và 'Số Hiệu', " +
                           "hệ thống sẽ tự động đọc và tra cứu thông tin tờ khai cho từng dòng.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công - Trả về kết quả tra cứu", content = {
                    @Content(schema = @Schema(implementation = ExcelTraCuuResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping(value = "/upload-excel", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadExcelAndTraCuu(
            @Parameter(description = "File Excel (.xlsx hoặc .xls) chứa cột 'Số Vận Đơn' và 'Số Hiệu'", 
                      required = true,
                      content = @Content(mediaType = "multipart/form-data"))
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Nhận yêu cầu upload file Excel: {}", file.getOriginalFilename());
            
            // Kiểm tra file
            if (file.isEmpty()) {
                throw new BusinessException("File Excel không được để trống");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls"))) {
                throw new BusinessException("Chỉ hỗ trợ file Excel (.xlsx, .xls)");
            }
            
            // Kiểm tra kích thước file (tối đa 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new BusinessException("Kích thước file không được vượt quá 10MB");
            }
            
            // Xử lý file Excel
            ExcelTraCuuResponse result = excelTraCuuService.processExcelAndTraCuu(
                file.getBytes(), 
                fileName
            );
            
            log.info("Upload và xử lý file Excel thành công: {} dòng, {} tìm thấy, {} không tìm thấy", 
                    result.getTotalRows(), result.getFoundRows(), result.getNotFoundRows());
            
            return ResponseHelper.ok(result);
            
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi upload file Excel: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi upload file Excel: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Upload Excel và export kết quả tra cứu ra Excel",
               description = "Nhận file Excel chứa cột 'Số Vận Đơn' và 'Số Hiệu', xử lý tra cứu và export kết quả ra file Excel với nhiều sheet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công - Trả về file Excel", content = {
                    @Content(schema = @Schema(implementation = byte[].class), mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping(value = "/export-excel-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> exportExcelTraCuuToExcel(
            @Parameter(description = "File Excel (.xlsx hoặc .xls) chứa cột 'Số Vận Đơn' và 'Số Hiệu'", 
                      required = true,
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Nhận yêu cầu upload Excel và export kết quả tra cứu: {}", file.getOriginalFilename());
            
            // Validate file
            if (file.isEmpty()) {
                throw new BusinessException("File Excel không được để trống");
            }
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || 
                (!originalFilename.toLowerCase().endsWith(".xlsx") && 
                 !originalFilename.toLowerCase().endsWith(".xls"))) {
                throw new BusinessException("File phải có định dạng .xlsx hoặc .xls");
            }
            
            // Xử lý file Excel và tra cứu
            ExcelTraCuuResponse traCuuResult = excelTraCuuService.processExcelAndTraCuu(
                file.getBytes(), file.getOriginalFilename());
            
            log.info("Xử lý Excel thành công: {} dòng, {} tìm thấy, {} lỗi", 
                    traCuuResult.getTotalRows(), traCuuResult.getFoundRows(), traCuuResult.getNotFoundRows());
            
            if (traCuuResult.getData().isEmpty()) {
                throw new BusinessException("Không có dữ liệu để export");
            }
            
            // Export ra Excel - sử dụng cùng method như /export-excel
            String fileName = String.format("ExcelTraCuuResult_%s.xlsx", System.currentTimeMillis());
            
            byte[] excelData = excelExportService.exportToKhaiTraCuuToExcel(traCuuResult.getData(), fileName);
            
            log.info("Export thành công kết quả Excel tra cứu ra Excel");
            
            // Tạo response với file Excel
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
            
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi export Excel: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi export Excel: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}
