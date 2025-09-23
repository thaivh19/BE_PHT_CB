package com.pht.controller;

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
import com.pht.entity.SysDisFeat;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysDisFeatCreateRequest;
import com.pht.model.request.SysDisFeatUpdateRequest;
import com.pht.service.SysDisFeatService;

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
@RequestMapping("/api/sys-dis-feat")
@Tag(name = "Quản lý phân quyền người dùng", description = "API quản lý phân quyền người dùng hệ thống")
public class SysDisFeatController {

    private final SysDisFeatService sysDisFeatService;

    @Operation(summary = "Lấy danh sách tất cả phân quyền người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysDisFeat.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllDisFeats() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tất cả phân quyền người dùng");
            
            var result = sysDisFeatService.findAll();
            
            log.info("Lấy danh sách phân quyền người dùng thành công, tổng số: {}", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy danh sách phân quyền người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy phân quyền người dùng theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysDisFeat.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phân quyền người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDisFeatById(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu lấy phân quyền người dùng theo ID: {}", id);
            
            SysDisFeat result = sysDisFeatService.getDisFeatById(id);
            
            log.info("Lấy phân quyền người dùng thành công với ID: {}", id);
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi lấy phân quyền người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy phân quyền người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy phân quyền người dùng theo User ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysDisFeat.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/by-user")
    public ResponseEntity<?> getDisFeatsByUserId(
            @Parameter(description = "ID của người dùng", example = "1")
            @RequestParam Long userId) {
        try {
            log.info("Nhận yêu cầu lấy phân quyền người dùng theo User ID: {}", userId);
            
            var result = sysDisFeatService.getByUserId(userId);
            
            log.info("Lấy phân quyền người dùng theo User ID thành công, tổng số: {}", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy phân quyền người dùng theo User ID: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới phân quyền người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysDisFeat.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create")
    public ResponseEntity<?> createDisFeat(@RequestBody SysDisFeatCreateRequest request) {
        try {
            log.info("Nhận yêu cầu tạo mới phân quyền người dùng: userId={}, funcId={}", 
                    request.getUserId(), request.getFuncId());
            
            SysDisFeat result = sysDisFeatService.createDisFeat(request);
            
            log.info("Tạo mới phân quyền người dùng thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tạo phân quyền người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tạo phân quyền người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật thông tin phân quyền người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysDisFeat.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phân quyền người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateDisFeat(@RequestBody SysDisFeatUpdateRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật phân quyền người dùng với ID: {}", request.getId());
            
            SysDisFeat result = sysDisFeatService.updateDisFeat(request);
            
            log.info("Cập nhật phân quyền người dùng thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi cập nhật phân quyền người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi cập nhật phân quyền người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa phân quyền người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phân quyền người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDisFeat(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu xóa phân quyền người dùng với ID: {}", id);
            
            sysDisFeatService.deleteDisFeat(id);
            
            log.info("Xóa phân quyền người dùng thành công với ID: {}", id);
            
            return ResponseHelper.ok("Xóa phân quyền người dùng thành công");
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi xóa phân quyền người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi xóa phân quyền người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}









