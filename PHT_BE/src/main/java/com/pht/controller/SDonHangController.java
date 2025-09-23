package com.pht.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pht.common.OrderBy;
import com.pht.common.helper.ResponseHelper;
import com.pht.common.model.ApiDataResponse;
import com.pht.entity.SDonHang;
import com.pht.model.request.SDonHangCreateRequest;
import com.pht.model.request.SDonHangSearchRequest;
import com.pht.model.request.SDonHangUpdateRequest;
import com.pht.model.request.DonHangKySoRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.service.SDonHangService;
import com.pht.service.DonHangKySoService;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/don-hang")
@Tag(name = "Đơn Hàng", description = "Quản lý đơn hàng")
public class SDonHangController {

    private final SDonHangService sDonHangService;
    private final DonHangKySoService donHangKySoService;

    // ========== BASIC CRUD ==========

    @Operation(summary = "Lấy danh sách tất cả đơn hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllDonHang(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "ngayTao") String sortBy,
                                          @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            CatalogSearchResponse<SDonHang> result = sDonHangService.getAllDonHangWithPagination(page, size, sortBy, sortDir);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy đơn hàng theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonHangById(@PathVariable Long id) {
        try {
            SDonHang result = sDonHangService.getDonHangById(id);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới đơn hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create")
    public ResponseEntity<?> createDonHang(@RequestBody SDonHangCreateRequest request) {
        try {
            log.info("Tạo mới đơn hàng: {}", request.getSoDonHang());
            SDonHang result = sDonHangService.createDonHang(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật đơn hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateDonHang(@RequestBody SDonHangUpdateRequest request) {
        try {
            log.info("Cập nhật đơn hàng ID: {}", request.getId());
            SDonHang result = sDonHangService.updateDonHang(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa đơn hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonHang(@PathVariable Long id) {
        try {
            log.info("Xóa đơn hàng với ID: {}", id);
            sDonHangService.deleteDonHang(id);
            return ResponseHelper.ok("Xóa đơn hàng thành công");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== SEARCH ENDPOINTS ==========

    @Operation(summary = "Tìm kiếm đơn hàng với phân trang")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = CatalogSearchResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchDonHang(@RequestBody SDonHangSearchRequest request) {
        try {
            log.info("Tìm kiếm đơn hàng với điều kiện: {}", request);
            CatalogSearchResponse<SDonHang> result = sDonHangService.searchDonHang(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tìm kiếm đơn hàng theo số đơn hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/search-by-sodonhang")
    public ResponseEntity<?> searchDonHangBySoDonHang(@RequestParam String soDonHang) {
        try {
            log.info("Tìm kiếm đơn hàng theo số đơn hàng: {}", soDonHang);
            SDonHang result = sDonHangService.findBySoDonHang(soDonHang);
            if (result != null) {
                return ResponseHelper.ok(result);
            } else {
                return ResponseHelper.notFound("Không tìm thấy đơn hàng với số đơn hàng: " + soDonHang);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== EXPORT ==========

    @Operation(summary = "Xuất dữ liệu đơn hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/export")
    public ResponseEntity<?> exportDonHang(@RequestBody SDonHangSearchRequest request) {
        try {
            log.info("Xuất dữ liệu đơn hàng với điều kiện: {}", request);
            List<SDonHang> result = sDonHangService.exportDonHang(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== KÝ SỐ ==========

    @Operation(summary = "Ký số đơn hàng với chữ ký số")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ký số thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng hoặc chữ ký số", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/ky-so")
    public ResponseEntity<?> kySoDonHang(@RequestBody DonHangKySoRequest request) {
        try {
            log.info("Nhận yêu cầu ký số đơn hàng ID: {} với serial number: {}", 
                    request.getIdDonHang(), request.getSerialNumber());
            
            String xmlKy = donHangKySoService.kySoDonHang(request.getIdDonHang(), request.getSerialNumber());
            
            log.info("Ký số thành công cho đơn hàng ID: {} với serial number: {}", 
                    request.getIdDonHang(), request.getSerialNumber());
            
            return ResponseHelper.ok(xmlKy);
            
        } catch (Exception ex) {
            log.error("Lỗi khi ký số đơn hàng ID {} với serial number {}: ", 
                    request.getIdDonHang(), request.getSerialNumber(), ex);
            return ResponseHelper.error(ex);
        }
    }

}
