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
import com.pht.entity.SmauPhiBienLai;
import com.pht.model.request.SmauPhiBienLaiCreateRequest;
import com.pht.model.request.SmauPhiBienLaiSearchRequest;
import com.pht.model.request.SmauPhiBienLaiUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.service.SmauPhiBienLaiService;

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
@RequestMapping("/api/mau-phi-bien-lai")
@Tag(name = "Mẫu Phí Biên Lai", description = "Quản lý danh mục mẫu phí biên lai")
public class SmauPhiBienLaiController {

    private final SmauPhiBienLaiService smauPhiBienLaiService;

    // ========== BASIC CRUD ==========

    @Operation(summary = "Lấy danh sách tất cả mẫu phí biên lai")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllMauPhiBienLai(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "kyHieu") String sortBy,
                                                @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            CatalogSearchResponse<SmauPhiBienLai> result = smauPhiBienLaiService.getAllMauPhiBienLaiWithPagination(page, size, sortBy, sortDir);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy mẫu phí biên lai theo ID")
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
    public ResponseEntity<?> getMauPhiBienLaiById(@PathVariable Long id) {
        try {
            SmauPhiBienLai result = smauPhiBienLaiService.getMauPhiBienLaiById(id);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới mẫu phí biên lai")
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
    public ResponseEntity<?> createMauPhiBienLai(@RequestBody SmauPhiBienLaiCreateRequest request) {
        try {
            SmauPhiBienLai result = smauPhiBienLaiService.createMauPhiBienLai(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật mẫu phí biên lai")
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
    public ResponseEntity<?> updateMauPhiBienLai(@RequestBody SmauPhiBienLaiUpdateRequest request) {
        try {
            SmauPhiBienLai result = smauPhiBienLaiService.updateMauPhiBienLai(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa mẫu phí biên lai")
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
    public ResponseEntity<?> deleteMauPhiBienLai(@PathVariable Long id) {
        try {
            smauPhiBienLaiService.deleteMauPhiBienLai(id);
            return ResponseHelper.ok("Xóa thành công");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== SEARCH ENDPOINTS ==========

    @Operation(summary = "Tìm kiếm mẫu phí biên lai với phân trang")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = CatalogSearchResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchMauPhiBienLai(@RequestBody SmauPhiBienLaiSearchRequest request) {
        try {
            CatalogSearchResponse<SmauPhiBienLai> result = smauPhiBienLaiService.searchMauPhiBienLai(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }

    // ========== EXPORT ==========

    @Operation(summary = "Xuất dữ liệu mẫu phí biên lai")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/export")
    public ResponseEntity<?> exportMauPhiBienLai(@RequestBody SmauPhiBienLaiSearchRequest request) {
        try {
            List<SmauPhiBienLai> result = smauPhiBienLaiService.exportMauPhiBienLai(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseHelper.error(ex);
        }
    }
}
