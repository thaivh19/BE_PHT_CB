package com.pht.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pht.common.OrderBy;
import com.pht.common.helper.ResponseHelper;
import com.pht.common.model.ApiDataResponse;
import java.util.List;

import com.pht.model.request.LayThongTinHaiQuanRequest;
import com.pht.model.response.ThongTinHaiQuanResponse;
import com.pht.service.HaiQuanService;

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
@RequestMapping("/api/hai-quan")
@Tag(name = "Hải quan", description = "API lấy thông tin từ hải quan")
public class HaiQuanController {

    private final HaiQuanService haiQuanService;

    @Operation(summary = "Lấy thông tin từ hải quan")
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
    @PostMapping("/lay-thong-tin")
    public ResponseEntity<?> layThongTinHaiQuan(@RequestBody LayThongTinHaiQuanRequest request) {
        try {
            log.info("Nhận yêu cầu lấy thông tin hải quan: {}", request);
            List<ThongTinHaiQuanResponse> result = haiQuanService.layThongTinHaiQuan(request);
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy thông tin hải quan: ", ex);
            return ResponseHelper.error(ex);
        }
    }


//    @Operation(summary = "Parse response String từ Hải quan")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Thành công", content = {
//                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
//            }),
//            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
//                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
//            }),
//            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
//                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
//            })
//    })
//    @PostMapping("/parse-response")
//    public ResponseEntity<?> parseHaiQuanResponse(@RequestBody ParseHaiQuanDataRequest request) {
//        try {
//            log.info("Nhận yêu cầu parse response từ Hải quan");
//            ThongTinHaiQuanResponse result = haiQuanService.parseHaiQuanResponse(request);
//            return ResponseHelper.ok(result);
//        } catch (Exception ex) {
//            log.error("Lỗi khi parse response từ Hải quan: ", ex);
//            return ResponseHelper.error(ex);
//        }
//    }
//
//    @Operation(summary = "Lấy XML response giả lập từ Hải quan")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Thành công", content = {
//                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
//            }),
//            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
//                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
//            }),
//            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
//                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
//            })
//    })
//    @PostMapping("/get-xml-response")
//    public ResponseEntity<?> getHaiQuanXmlResponse(@RequestBody LayThongTinHaiQuanRequest request) {
//        try {
//            log.info("Nhận yêu cầu lấy XML response giả lập từ Hải quan");
//            var result = haiQuanService.getHaiQuanXmlResponse(request);
//            return ResponseHelper.ok(result);
//        } catch (Exception ex) {
//            log.error("Lỗi khi lấy XML response từ Hải quan: ", ex);
//            return ResponseHelper.error(ex);
//        }
//    }
}
