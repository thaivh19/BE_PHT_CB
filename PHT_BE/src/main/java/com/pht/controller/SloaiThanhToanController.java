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
import com.pht.entity.SloaiThanhToan;
import com.pht.model.request.SloaiThanhToanCreateRequest;
import com.pht.model.request.SloaiThanhToanSearchRequest;
import com.pht.model.request.SloaiThanhToanUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.service.SloaiThanhToanService;

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
@RequestMapping("/api/loai-thanh-toan")
@Tag(name = "Loại Thanh Toán", description = "Quản lý danh mục loại thanh toán")
public class SloaiThanhToanController {

    private final SloaiThanhToanService sloaiThanhToanService;

    // ========== BASIC CRUD ==========

    @Operation(summary = "Lấy danh sách tất cả loại thanh toán")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllLoaiThanhToan(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "maLoaiThanhToan") String sortBy,
                                               @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            CatalogSearchResponse<SloaiThanhToan> result = sloaiThanhToanService.getAllLoaiThanhToanWithPagination(page, size, sortBy, sortDir);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy loại thanh toán theo ID")
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
    public ResponseEntity<?> getLoaiThanhToanById(@PathVariable Long id) {
        try {
            SloaiThanhToan result = sloaiThanhToanService.getLoaiThanhToanById(id);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới loại thanh toán")
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
    public ResponseEntity<?> createLoaiThanhToan(@RequestBody SloaiThanhToanCreateRequest request) {
        try {
            SloaiThanhToan result = sloaiThanhToanService.createLoaiThanhToan(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật loại thanh toán")
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
    public ResponseEntity<?> updateLoaiThanhToan(@RequestBody SloaiThanhToanUpdateRequest request) {
        try {
            SloaiThanhToan result = sloaiThanhToanService.updateLoaiThanhToan(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa loại thanh toán")
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
    public ResponseEntity<?> deleteLoaiThanhToan(@PathVariable Long id) {
        try {
            sloaiThanhToanService.deleteLoaiThanhToan(id);
            return ResponseHelper.ok("Xóa thành công");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== SEARCH ENDPOINTS ==========

    @Operation(summary = "Tìm kiếm loại thanh toán với phân trang")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = CatalogSearchResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchLoaiThanhToan(@RequestBody SloaiThanhToanSearchRequest request) {
        try {
            CatalogSearchResponse<SloaiThanhToan> result = sloaiThanhToanService.searchLoaiThanhToan(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== EXPORT ==========

    @Operation(summary = "Xuất dữ liệu loại thanh toán")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/export")
    public ResponseEntity<?> exportLoaiThanhToan(@RequestBody SloaiThanhToanSearchRequest request) {
        try {
            List<SloaiThanhToan> result = sloaiThanhToanService.exportLoaiThanhToan(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }
}
