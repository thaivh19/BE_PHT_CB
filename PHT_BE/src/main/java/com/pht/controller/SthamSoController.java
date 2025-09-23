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
import com.pht.entity.SthamSo;
import com.pht.exception.BusinessException;
import com.pht.model.request.SthamSoCreateRequest;
import com.pht.model.request.SthamSoUpdateRequest;
import com.pht.service.SthamSoService;

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
@RequestMapping("/api/stham-so")
@Tag(name = "Tham số hệ thống", description = "API quản lý tham số hệ thống")
public class SthamSoController {

    private final SthamSoService sthamSoService;

    @Operation(summary = "Lấy danh sách tất cả tham số")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SthamSo.class), mediaType = "application/json")
            })
    })
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tham số");
            List<SthamSo> result = sthamSoService.getAll();
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách tham số: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy thông tin tham số theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SthamSo.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tham số", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID tham số", example = "1")
            @PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu lấy tham số với ID: {}", id);
            SthamSo result = sthamSoService.getById(id);
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi lấy tham số: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy tham số: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tìm tham số theo mã")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SthamSo.class), mediaType = "application/json")
            })
    })
    @GetMapping("/by-ma")
    public ResponseEntity<?> findByMaTs(
            @Parameter(description = "Mã tham số", example = "MAX_FILE_SIZE")
            @RequestParam String maTs) {
        try {
            log.info("Nhận yêu cầu tìm tham số theo mã: {}", maTs);
            SthamSo result = sthamSoService.findByMaTs(maTs);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi tìm tham số theo mã: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới tham số")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SthamSo.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody SthamSoCreateRequest request) {
        try {
            log.info("Nhận yêu cầu tạo mới tham số: {}", request.getMaTs());
            SthamSo result = sthamSoService.create(request);
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tạo tham số: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tạo tham số: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật tham số")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SthamSo.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tham số", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PutMapping
    public ResponseEntity<?> update(@RequestBody SthamSoUpdateRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật tham số với ID: {}", request.getId());
            SthamSo result = sthamSoService.update(request);
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi cập nhật tham số: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi cập nhật tham số: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa tham số")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tham số", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(
            @Parameter(description = "ID tham số", example = "1")
            @PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu xóa tham số với ID: {}", id);
            sthamSoService.deleteById(id);
            return ResponseHelper.ok("Xóa tham số thành công");
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi xóa tham số: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi xóa tham số: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}









