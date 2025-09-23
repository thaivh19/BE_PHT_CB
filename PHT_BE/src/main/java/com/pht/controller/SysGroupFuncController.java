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
import com.pht.entity.SysGroupFunc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysGroupFuncCreateRequest;
import com.pht.model.request.SysGroupFuncUpdateRequest;
import com.pht.service.SysGroupFuncService;

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
@RequestMapping("/api/sys-group-func")
@Tag(name = "Quản lý phân quyền nhóm", description = "API quản lý phân quyền nhóm hệ thống")
public class SysGroupFuncController {

    private final SysGroupFuncService sysGroupFuncService;

    @Operation(summary = "Lấy danh sách tất cả phân quyền nhóm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllGroupFuncs() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tất cả phân quyền nhóm");
            
            var result = sysGroupFuncService.findAll();
            
            log.info("Lấy danh sách phân quyền nhóm thành công, tổng số: {}", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy danh sách phân quyền nhóm: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy phân quyền nhóm theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phân quyền nhóm", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupFuncById(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu lấy phân quyền nhóm theo ID: {}", id);
            
            SysGroupFunc result = sysGroupFuncService.getGroupFuncById(id);
            
            log.info("Lấy phân quyền nhóm thành công với ID: {}", id);
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi lấy phân quyền nhóm: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy phân quyền nhóm: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy phân quyền nhóm theo Group ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/by-group")
    public ResponseEntity<?> getGroupFuncsByGroupId(
            @Parameter(description = "ID của nhóm", example = "1")
            @RequestParam Long groupId) {
        try {
            log.info("Nhận yêu cầu lấy phân quyền nhóm theo Group ID: {}", groupId);
            
            var result = sysGroupFuncService.getByGroupId(groupId);
            
            log.info("Lấy phân quyền nhóm theo Group ID thành công, tổng số: {}", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy phân quyền nhóm theo Group ID: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới phân quyền nhóm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create")
    public ResponseEntity<?> createGroupFunc(@RequestBody SysGroupFuncCreateRequest request) {
        try {
            log.info("Nhận yêu cầu tạo mới phân quyền nhóm: groupId={}, funcId={}", 
                    request.getGroupId(), request.getFuncId());
            
            SysGroupFunc result = sysGroupFuncService.createGroupFunc(request);
            
            log.info("Tạo mới phân quyền nhóm thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tạo phân quyền nhóm: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tạo phân quyền nhóm: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật thông tin phân quyền nhóm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phân quyền nhóm", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateGroupFunc(@RequestBody SysGroupFuncUpdateRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật phân quyền nhóm với ID: {}", request.getId());
            
            SysGroupFunc result = sysGroupFuncService.updateGroupFunc(request);
            
            log.info("Cập nhật phân quyền nhóm thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi cập nhật phân quyền nhóm: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi cập nhật phân quyền nhóm: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa phân quyền nhóm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phân quyền nhóm", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGroupFunc(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu xóa phân quyền nhóm với ID: {}", id);
            
            sysGroupFuncService.deleteGroupFunc(id);
            
            log.info("Xóa phân quyền nhóm thành công với ID: {}", id);
            
            return ResponseHelper.ok("Xóa phân quyền nhóm thành công");
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi xóa phân quyền nhóm: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi xóa phân quyền nhóm: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}









