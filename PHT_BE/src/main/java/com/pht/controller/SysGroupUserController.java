package com.pht.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pht.common.helper.ResponseHelper;
import com.pht.entity.SysGroupUser;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysGroupUserCreateRequest;
import com.pht.model.request.SysGroupUserUpdateRequest;
import com.pht.service.SysGroupUserService;

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
@RequestMapping("/api/sys-group-user")
@Tag(name = "Quản lý nhóm người dùng", description = "API quản lý nhóm người dùng hệ thống")
public class SysGroupUserController {

    private final SysGroupUserService sysGroupUserService;

    @Operation(summary = "Lấy danh sách tất cả nhóm người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupUser.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllGroupUsers() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tất cả nhóm người dùng");
            
            var result = sysGroupUserService.findAll();
            
            log.info("Lấy danh sách nhóm người dùng thành công, tổng số: {}", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy danh sách nhóm người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy nhóm người dùng theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupUser.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhóm người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupUserById(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu lấy nhóm người dùng theo ID: {}", id);
            
            SysGroupUser result = sysGroupUserService.getGroupUserById(id);
            
            log.info("Lấy nhóm người dùng thành công với ID: {}", id);
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi lấy nhóm người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy nhóm người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới nhóm người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupUser.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create")
    public ResponseEntity<?> createGroupUser(@RequestBody SysGroupUserCreateRequest request) {
        try {
            log.info("Nhận yêu cầu tạo mới nhóm người dùng: {}", request.getGroupName());
            
            SysGroupUser result = sysGroupUserService.createGroupUser(request);
            
            log.info("Tạo mới nhóm người dùng thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tạo nhóm người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tạo nhóm người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật thông tin nhóm người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysGroupUser.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhóm người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateGroupUser(@RequestBody SysGroupUserUpdateRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật nhóm người dùng với ID: {}", request.getId());
            
            SysGroupUser result = sysGroupUserService.updateGroupUser(request);
            
            log.info("Cập nhật nhóm người dùng thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi cập nhật nhóm người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi cập nhật nhóm người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa nhóm người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhóm người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGroupUser(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu xóa nhóm người dùng với ID: {}", id);
            
            sysGroupUserService.deleteGroupUser(id);
            
            log.info("Xóa nhóm người dùng thành công với ID: {}", id);
            
            return ResponseHelper.ok("Xóa nhóm người dùng thành công");
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi xóa nhóm người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi xóa nhóm người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}









