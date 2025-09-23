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
import com.pht.entity.SbieuCuoc;
import com.pht.model.request.SbieuCuocCreateRequest;
import com.pht.model.request.SbieuCuocSearchRequest;
import com.pht.model.request.SbieuCuocUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.service.SbieuCuocService;

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
@RequestMapping("/api/bieu-cuoc")
@Tag(name = "Biểu Cước", description = "Quản lý danh mục biểu cước")
public class SbieuCuocController {

    private final SbieuCuocService sbieuCuocService;

    // ========== BASIC CRUD ==========

    @Operation(summary = "Lấy danh sách tất cả biểu cước")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllBieuCuoc(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "maBieuCuoc") String sortBy,
                                          @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            CatalogSearchResponse<SbieuCuoc> result = sbieuCuocService.getAllBieuCuocWithPagination(page, size, sortBy, sortDir);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy biểu cước theo ID")
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
    public ResponseEntity<?> getBieuCuocById(@PathVariable Long id) {
        try {
            SbieuCuoc result = sbieuCuocService.getBieuCuocById(id);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới biểu cước")
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
    public ResponseEntity<?> createBieuCuoc(@RequestBody SbieuCuocCreateRequest request) {
        try {
            SbieuCuoc result = sbieuCuocService.createBieuCuoc(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật biểu cước")
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
    public ResponseEntity<?> updateBieuCuoc(@RequestBody SbieuCuocUpdateRequest request) {
        try {
            SbieuCuoc result = sbieuCuocService.updateBieuCuoc(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa biểu cước")
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
    public ResponseEntity<?> deleteBieuCuoc(@PathVariable Long id) {
        try {
            sbieuCuocService.deleteBieuCuoc(id);
            return ResponseHelper.ok("Xóa thành công");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== SEARCH ENDPOINTS ==========

    @Operation(summary = "Tìm kiếm biểu cước với phân trang")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = CatalogSearchResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchBieuCuoc(@RequestBody SbieuCuocSearchRequest request) {
        try {
            CatalogSearchResponse<SbieuCuoc> result = sbieuCuocService.searchBieuCuoc(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== EXPORT ==========

    @Operation(summary = "Xuất dữ liệu biểu cước")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/export")
    public ResponseEntity<?> exportBieuCuoc(@RequestBody SbieuCuocSearchRequest request) {
        try {
            List<SbieuCuoc> result = sbieuCuocService.exportBieuCuoc(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }
}
