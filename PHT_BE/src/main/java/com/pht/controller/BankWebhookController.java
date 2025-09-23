package com.pht.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.pht.model.request.BankWebhookRequest;
import com.pht.model.response.BankWebhookResponse;
import com.pht.service.BankWebhookService;
import java.util.List;

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
@RequestMapping("/api/bank-webhook")
@Tag(name = "Bank Webhook", description = "API nhận dữ liệu từ ngân hàng")
public class BankWebhookController {

    private final BankWebhookService bankWebhookService;

    @PostMapping(value = "/payment-notification", 
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Nhận thông báo thanh toán từ ngân hàng", 
               description = "API nhận webhook từ ngân hàng khi có giao dịch thanh toán thành công")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xử lý thành công",
                    content = @Content(schema = @Schema(implementation = BankWebhookResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<BankWebhookResponse> receivePaymentNotification(@Valid @RequestBody BankWebhookRequest request) {
        log.info("Nhận webhook từ ngân hàng: transId={}, amount={}", 
                request != null ? request.getTransId() : "null", 
                request != null ? request.getAmount() : "null");
        
        try {
            BankWebhookResponse response = bankWebhookService.processPaymentNotification(request);
            
            if ("00".equals(response.getErrorCode())) {
                log.info("Xử lý webhook thành công: transId={}", response.getTransId());
                return ResponseEntity.ok(response);
            } else {
                log.warn("Xử lý webhook thất bại: transId={}, errorCode={}, errorDesc={}", 
                        response.getTransId(), response.getErrorCode(), response.getErrorDesc());
                return ResponseEntity.ok(response); // Vẫn trả về 200 nhưng có errorCode
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý webhook từ ngân hàng: ", e);
            
            // Tạo response lỗi
            BankWebhookResponse errorResponse = new BankWebhookResponse();
            errorResponse.setTransId(request.getTransId());
            errorResponse.setProviderId(request.getProviderId());
            errorResponse.setErrorCode("99");
            errorResponse.setErrorDesc("Lỗi hệ thống");
            errorResponse.setSignature("");
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    @PostMapping(value = "/simulate-payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Giả lập nhận thông báo thanh toán", description = "Tìm các đơn có tờ khai TT_NH=00, tạo json mô phỏng và gọi xử lý webhook")
    public ResponseEntity<List<BankWebhookResponse>> simulatePaymentNotifications(@RequestBody List<String> soDonHangList) {
        log.info("Giả lập webhook cho các soDonHang: {}", soDonHangList);
        List<BankWebhookResponse> responses = bankWebhookService.simulatePaymentForOrders(soDonHangList);
        return ResponseEntity.ok(responses);
    }
}
