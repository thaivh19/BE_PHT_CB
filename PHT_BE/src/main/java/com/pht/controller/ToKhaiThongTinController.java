package com.pht.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pht.common.helper.ResponseHelper;
import com.pht.entity.StoKhai;
import com.pht.exception.BusinessException;
import com.pht.model.request.NotificationRequest;
import com.pht.model.request.ToKhaiFilterRequest;
import com.pht.model.request.ToKhaiThongTinRequest;
import com.pht.model.request.UpdateTrangThaiPhatHanhRequest;
import com.pht.model.request.UpdateTrangThaiRequest;
import com.pht.model.response.NotificationResponse;
import com.pht.service.ToKhaiThongTinService;

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
@RequestMapping("/api/tokhai-thongtin")
@Tag(name = "Tờ khai thông tin", description = "API quản lý tờ khai thông tin")
public class ToKhaiThongTinController {

    private final ToKhaiThongTinService toKhaiThongTinService;

    @Operation(summary = "Lấy danh sách tờ khai theo trạng thái")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/danh-sach")
    public ResponseEntity<?> layDanhSachToKhai(
            @Parameter(description = "Trạng thái tờ khai", example = "02")
            @RequestParam(defaultValue = "02") String trangThai) {
        try {
            log.info("Nhận yêu cầu lấy danh sách tờ khai với trạng thái: {}", trangThai);
            
            List<StoKhai> toKhaiList = toKhaiThongTinService.findByTrangThai(trangThai);
            
            log.info("Tìm thấy {} tờ khai với trạng thái {}", toKhaiList.size(), trangThai);
            
            return ResponseHelper.ok(toKhaiList);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách tờ khai: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy danh sách tờ khai trạng thái 02")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/ds-nphi")
    public ResponseEntity<?> layDanhSachToKhaiTrangThai02() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tờ khai trạng thái 02");
            
            List<StoKhai> toKhaiList = toKhaiThongTinService.findByTrangThai("02");
            
            log.info("Tìm thấy {} tờ khai với trạng thái 02", toKhaiList.size());
            
            return ResponseHelper.ok(toKhaiList);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách tờ khai trạng thái 02: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy danh sách tờ khai trạng thái 02 và 03")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/ds-nphi-03")
    public ResponseEntity<?> layDanhSachToKhaiTrangThai02va03() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tờ khai trạng thái 02 và 03");
            
             // Lấy tờ khai trạng thái 02
             List<StoKhai> toKhai02List = toKhaiThongTinService.findByTrangThai("02");
             log.info("Tìm thấy {} tờ khai với trạng thái 02", toKhai02List.size());

            // Lấy tờ khai trạng thái 03
            List<StoKhai> toKhai03List = toKhaiThongTinService.findByTrangThai("03");
            log.info("Tìm thấy {} tờ khai với trạng thái 03", toKhai03List.size());
            
         // Lấy tờ khai trạng thái 04
            List<StoKhai> toKhai04List = toKhaiThongTinService.findByTrangThai("04");
            log.info("Tìm thấy {} tờ khai với trạng thái 04", toKhai04List.size());
            
            // Gộp 2 danh sách
            List<StoKhai> combinedList = new ArrayList<>();
            combinedList.addAll(toKhai02List);
            combinedList.addAll(toKhai03List);
            combinedList.addAll(toKhai04List);
            log.info("Tổng cộng tìm thấy {} tờ khai với trạng thái 02  ,03 ,04", combinedList.size());
            
            return ResponseHelper.ok(combinedList);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách tờ khai trạng thái 02 và 03: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy danh sách tất cả tờ khai thông tin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/all")
    public ResponseEntity<?> layDanhSachTatCaToKhai() {
        try {
            log.info("Nhận yêu cầu lấy danh sách tất cả tờ khai thông tin");
            
            List<StoKhai> toKhaiList = toKhaiThongTinService.getAllToKhaiThongTin();
            
            log.info("Tìm thấy {} tờ khai thông tin", toKhaiList.size());
            
            return ResponseHelper.ok(toKhaiList);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách tất cả tờ khai thông tin: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy tờ khai thông tin theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> layStoKhaiTheoId(
            @Parameter(description = "ID tờ khai thông tin", example = "1")
            @PathVariable Long id) {
        try {
            log.info("Nhận yêu cầu lấy tờ khai thông tin theo ID: {}", id);
            
            StoKhai toKhai = toKhaiThongTinService.getToKhaiThongTinById(id);
            
            log.info("Tìm thấy tờ khai thông tin với ID: {}", id);
            
            return ResponseHelper.ok(toKhai);
        } catch (BusinessException ex) {
            log.error("Lỗi business khi lấy tờ khai thông tin theo ID {}: {}", id, ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy tờ khai thông tin theo ID {}: ", id, ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo mới tờ khai thông tin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create")
    public ResponseEntity<?> taoMoiStoKhai(@RequestBody ToKhaiThongTinRequest request) {
        try {
            log.info("Nhận yêu cầu tạo mới tờ khai thông tin");
            
            StoKhai toKhai = toKhaiThongTinService.createToKhaiThongTin(request);
            
            log.info("Tạo mới tờ khai thông tin thành công với ID: {}", toKhai.getId());
            
            return ResponseHelper.ok(toKhai);
        } catch (BusinessException ex) {
            log.error("Lỗi business khi tạo mới tờ khai thông tin: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi khi tạo mới tờ khai thông tin: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo thông báo và thay đổi trạng thái sang 02")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/notification")
    public ResponseEntity<?> taoThongBaoVaThayDoiTrangThai(@RequestBody NotificationRequest request) {
        try {
            log.info("Nhận yêu cầu tạo thông báo và thay đổi trạng thái sang 02");
            
            NotificationResponse response = toKhaiThongTinService.createNotification(request);
            
            log.info("Tạo thông báo và thay đổi trạng thái thành công");
            
            return ResponseHelper.ok(response);
        } catch (BusinessException ex) {
            log.error("Lỗi business khi tạo thông báo: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi khi tạo thông báo: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật trạng thái tờ khai thông tin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PutMapping("/update-status")
    public ResponseEntity<?> capNhatTrangThai(@RequestBody UpdateTrangThaiRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật trạng thái tờ khai thông tin");
            
            StoKhai toKhai = toKhaiThongTinService.updateTrangThai(request);
            
            log.info("Cập nhật trạng thái tờ khai thông tin thành công với ID: {}", toKhai.getId());
            
            return ResponseHelper.ok(toKhai);
        } catch (BusinessException ex) {
            log.error("Lỗi business khi cập nhật trạng thái: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi khi cập nhật trạng thái: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật trạng thái phát hành tờ khai thông tin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PutMapping("/update-publication-status")
    public ResponseEntity<?> capNhatTrangThaiPhatHanh(@RequestBody UpdateTrangThaiPhatHanhRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật trạng thái phát hành tờ khai thông tin");
            
            StoKhai toKhai = toKhaiThongTinService.updateTrangThaiPhatHanh(request);
            
            log.info("Cập nhật trạng thái phát hành tờ khai thông tin thành công với ID: {}", toKhai.getId());
            
            return ResponseHelper.ok(toKhai);
        } catch (BusinessException ex) {
            log.error("Lỗi business khi cập nhật trạng thái phát hành: {}", ex.getMessage());
            return ResponseHelper.error(ex);
        } catch (Exception ex) {
            log.error("Lỗi khi cập nhật trạng thái phát hành: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lọc tờ khai theo ngày và trạng thái")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.model.ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu request không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = com.pht.common.OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/filter")
    public ResponseEntity<?> locToKhaiTheoNgayVaTrangThai(@RequestBody ToKhaiFilterRequest request) {
        try {
            log.info("Nhận yêu cầu lọc tờ khai theo ngày từ {} đến {} và trạng thái {}", 
                    request.getTuNgay(), request.getDenNgay(), request.getTrangThai());
            
            List<StoKhai> toKhaiList = toKhaiThongTinService.filterToKhai(request);
            
            log.info("Tìm thấy {} tờ khai thỏa mãn điều kiện lọc", toKhaiList.size());
            
            return ResponseHelper.ok(toKhaiList);
        } catch (Exception ex) {
            log.error("Lỗi khi lọc tờ khai theo ngày và trạng thái: ", ex);
            return ResponseHelper.error(ex);
        }
    }
}
