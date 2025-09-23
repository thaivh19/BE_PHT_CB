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
import com.pht.entity.SphuongThucVanTai;
import com.pht.model.request.SphuongThucVanTaiCreateRequest;
import com.pht.model.request.SphuongThucVanTaiSearchRequest;
import com.pht.model.request.SphuongThucVanTaiUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.service.SphuongThucVanTaiService;

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
@RequestMapping("/api/phuong-thuc-van-tai")
@Tag(name = "Phương Thức Vận Tải", description = "Quản lý danh mục phương thức vận tải")
public class SphuongThucVanTaiController {

    private final SphuongThucVanTaiService sphuongThucVanTaiService;

    // ========== BASIC CRUD ==========

    @Operation(summary = "Lấy danh sách tất cả phương thức vận tải")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllPhuongThucVanTai(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "maPtvt") String sortBy,
                                                   @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            CatalogSearchResponse<SphuongThucVanTai> result = sphuongThucVanTaiService.getAllPhuongThucVanTaiWithPagination(page, size, sortBy, sortDir);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy phương thức vận tải theo ID")
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
    public ResponseEntity<?> getPhuongThucVanTaiById(@PathVariable Long id) {
        try {
            SphuongThucVanTai result = sphuongThucVanTaiService.getPhuongThucVanTaiById(id);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới phương thức vận tải")
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
    public ResponseEntity<?> createPhuongThucVanTai(@RequestBody SphuongThucVanTaiCreateRequest request) {
        try {
            SphuongThucVanTai result = sphuongThucVanTaiService.createPhuongThucVanTai(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật phương thức vận tải")
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
    public ResponseEntity<?> updatePhuongThucVanTai(@RequestBody SphuongThucVanTaiUpdateRequest request) {
        try {
            SphuongThucVanTai result = sphuongThucVanTaiService.updatePhuongThucVanTai(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa phương thức vận tải")
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
    public ResponseEntity<?> deletePhuongThucVanTai(@PathVariable Long id) {
        try {
            sphuongThucVanTaiService.deletePhuongThucVanTai(id);
            return ResponseHelper.ok("Xóa thành công");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== SEARCH ENDPOINTS ==========

    @Operation(summary = "Tìm kiếm phương thức vận tải với phân trang")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = CatalogSearchResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchPhuongThucVanTai(@RequestBody SphuongThucVanTaiSearchRequest request) {
        try {
            CatalogSearchResponse<SphuongThucVanTai> result = sphuongThucVanTaiService.searchPhuongThucVanTai(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== EXPORT ==========

    @Operation(summary = "Xuất dữ liệu phương thức vận tải")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/export")
    public ResponseEntity<?> exportPhuongThucVanTai(@RequestBody SphuongThucVanTaiSearchRequest request) {
        try {
            List<SphuongThucVanTai> result = sphuongThucVanTaiService.exportPhuongThucVanTai(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }
}
