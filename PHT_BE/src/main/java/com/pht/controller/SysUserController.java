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
import com.pht.dto.UserPermissionDto;
import com.pht.entity.SysUser;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysUserCreateRequest;
import com.pht.model.request.SysUserUpdateRequest;
import com.pht.service.SysUserService;
import com.pht.service.UserPermissionService;

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
@RequestMapping("/api/sys-user")
@Tag(name = "Quản lý người dùng hệ thống", description = "API quản lý người dùng hệ thống")
public class SysUserController {

    private final SysUserService sysUserService;
    private final UserPermissionService userPermissionService;

    @Operation(summary = "Lấy danh sách tất cả người dùng hệ thống")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysUser.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tất cả người dùng hệ thống");
            
            List<SysUser> result = sysUserService.getAllUsers();
            
            log.info("Lấy danh sách người dùng thành công, tổng số: {}", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy danh sách người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysUser.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody SysUserCreateRequest request) {
        try {
            log.info("Nhận yêu cầu tạo mới người dùng: {}", request.getUsername());
            
            SysUser result = sysUserService.createUser(request);
            
            log.info("Tạo mới người dùng thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tạo người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tạo người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysUser.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody SysUserUpdateRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật người dùng với ID: {}", request.getId());
            
            SysUser result = sysUserService.updateUser(request);
            
            log.info("Cập nhật người dùng thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi cập nhật người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi cập nhật người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu xóa người dùng với ID: {}", id);
            
            sysUserService.deleteUser(id);
            
            log.info("Xóa người dùng thành công với ID: {}", id);
            
            return ResponseHelper.ok("Xóa người dùng thành công");
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi xóa người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi xóa người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy quyền của người dùng theo username", 
               description = "Trả về thông tin người dùng và danh sách các function được phép (từ group, loại trừ disabled)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = UserPermissionDto.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/view")
    public ResponseEntity<?> getUserPermissionsByUsername(
            @Parameter(description = "Username của người dùng", example = "admin")
            @RequestParam String username) {
        try {
            log.info("Nhận yêu cầu lấy quyền người dùng theo username: {}", username);
            
            UserPermissionDto result = userPermissionService.getUserPermissionsByUsername(username);
            
            log.info("Lấy quyền người dùng thành công cho username: {}, có {} function được phép", 
                    username, result.getAllowedFunctions().size());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi lấy quyền người dùng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy quyền người dùng: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}
