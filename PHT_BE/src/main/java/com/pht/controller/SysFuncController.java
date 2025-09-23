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
import com.pht.entity.SysFunc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysFuncCreateRequest;
import com.pht.model.request.SysFuncUpdateRequest;
import com.pht.service.SysFuncService;

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
@RequestMapping("/api/sys-func")
@Tag(name = "Quản lý chức năng", description = "API quản lý chức năng hệ thống")
public class SysFuncController {

    private final SysFuncService sysFuncService;

    @Operation(summary = "Lấy danh sách tất cả chức năng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllFuncs() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tất cả chức năng");
            
            var result = sysFuncService.findAll();
            
            log.info("Lấy danh sách chức năng thành công, tổng số: {}", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy danh sách chức năng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy chức năng theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chức năng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getFuncById(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu lấy chức năng theo ID: {}", id);
            
            SysFunc result = sysFuncService.getFuncById(id);
            
            log.info("Lấy chức năng thành công với ID: {}", id);
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi lấy chức năng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi lấy chức năng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới chức năng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create")
    public ResponseEntity<?> createFunc(@RequestBody SysFuncCreateRequest request) {
        try {
            log.info("Nhận yêu cầu tạo mới chức năng: {}", request.getFuncName());
            
            SysFunc result = sysFuncService.createFunc(request);
            
            log.info("Tạo mới chức năng thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi tạo chức năng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi tạo chức năng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật thông tin chức năng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = SysFunc.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chức năng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateFunc(@RequestBody SysFuncUpdateRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật chức năng với ID: {}", request.getId());
            
            SysFunc result = sysFuncService.updateFunc(request);
            
            log.info("Cập nhật chức năng thành công với ID: {}", result.getId());
            
            return ResponseHelper.ok(result);
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi cập nhật chức năng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi cập nhật chức năng: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Xóa chức năng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chức năng", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = BusinessException.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFunc(@PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu xóa chức năng với ID: {}", id);
            
            sysFuncService.deleteFunc(id);
            
            log.info("Xóa chức năng thành công với ID: {}", id);
            
            return ResponseHelper.ok("Xóa chức năng thành công");
        } catch (BusinessException ex) {
            log.error("Lỗi nghiệp vụ khi xóa chức năng: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi hệ thống khi xóa chức năng: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}









