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

import com.pht.common.helper.ResponseHelper;
import com.pht.entity.NgayLv;
import com.pht.exception.BusinessException;
import com.pht.model.request.NgayLvCreateRequest;
import com.pht.model.request.NgayLvUpdateRequest;
import com.pht.service.NgayLvService;

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
@RequestMapping("/api/ngay-lv")
@Tag(name = "Ngày làm việc", description = "API quản lý ngày làm việc")
public class NgayLvController {

    private final NgayLvService ngayLvService;

    @Operation(summary = "Lấy danh sách tất cả ngày làm việc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = NgayLv.class), mediaType = "application/json")
            })
    })
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            log.info("Nhận yêu cầu lấy danh sách ngày làm việc");
            List<NgayLv> result = ngayLvService.getAll();
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách ngày làm việc: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy thông tin ngày làm việc theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = NgayLv.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ngày làm việc", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID ngày làm việc", example = "1")
            @PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu lấy ngày làm việc với ID: {}", id);
            NgayLv result = ngayLvService.getById(id);
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi lấy ngày làm việc: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy ngày làm việc: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tìm ngày làm việc theo trạng thái")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = NgayLv.class), mediaType = "application/json")
            })
    })
    @GetMapping("/by-trang-thai")
    public ResponseEntity<?> findByTrangThai(
            @Parameter(description = "Trạng thái (1: Làm việc, 0: Nghỉ)", example = "1")
            @RequestParam String trangThai) {
        try {
            log.info("Nhận yêu cầu tìm ngày làm việc theo trạng thái: {}", trangThai);
            List<NgayLv> result = ngayLvService.findByTrangThai(trangThai);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi tìm ngày làm việc theo trạng thái: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tìm ngày làm việc theo cột")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = NgayLv.class), mediaType = "application/json")
            })
    })
    @GetMapping("/by-cot")
    public ResponseEntity<?> findByCot(
            @Parameter(description = "Cột phân loại", example = "THANG")
            @RequestParam String cot) {
        try {
            log.info("Nhận yêu cầu tìm ngày làm việc theo cột: {}", cot);
            List<NgayLv> result = ngayLvService.findByCot(cot);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi tìm ngày làm việc theo cột: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới ngày làm việc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = NgayLv.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NgayLvCreateRequest request) {
        try {
            log.info("Nhận yêu cầu tạo mới ngày làm việc: {}", request.getNgayLv());
            NgayLv result = ngayLvService.create(request);
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tạo ngày làm việc: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tạo ngày làm việc: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật ngày làm việc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = NgayLv.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ngày làm việc", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PutMapping
    public ResponseEntity<?> update(@RequestBody NgayLvUpdateRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật ngày làm việc với ID: {}", request.getId());
            NgayLv result = ngayLvService.update(request);
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi cập nhật ngày làm việc: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi cập nhật ngày làm việc: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa ngày làm việc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ngày làm việc", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(
            @Parameter(description = "ID ngày làm việc", example = "1")
            @PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu xóa ngày làm việc với ID: {}", id);
            ngayLvService.deleteById(id);
            return ResponseHelper.ok("Xóa ngày làm việc thành công");
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi xóa ngày làm việc: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi xóa ngày làm việc: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}









