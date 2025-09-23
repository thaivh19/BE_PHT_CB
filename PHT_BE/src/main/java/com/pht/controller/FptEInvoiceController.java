package com.pht.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Base64;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pht.common.OrderBy;
import com.pht.common.helper.ResponseHelper;
import com.pht.common.model.ApiDataResponse;
import com.pht.exception.BusinessException;
import com.pht.model.request.CancelInvoiceRequest;
import com.pht.model.request.CreateIcrRequest;
import com.pht.model.request.DeleteInvoiceRequest;
import com.pht.model.request.ReplaceInvoiceRequest;
import com.pht.model.request.SearchInvoiceRequest;
import com.pht.model.request.UpdateTrangThaiPhatHanhRequest;
import com.pht.model.response.CancelInvoiceResponse;
import com.pht.model.response.DeleteInvoiceResponse;
import com.pht.model.response.ReplaceInvoiceResponse;
import com.pht.service.ToKhaiThongTinService;
import com.pht.service.SBienLaiService;
import com.pht.util.PdfConverterUtil;

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
@RequestMapping("/api/fpt-einvoice")
@Tag(name = "FPT eInvoice", description = "API tích hợp với FPT eInvoice")
public class FptEInvoiceController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ToKhaiThongTinService toKhaiThongTinService;
    private final SBienLaiService sBienLaiService;
    private final PdfConverterUtil pdfConverterUtil;

    @Value("${fpt.einvoice.api.url:https://api-uat.einvoice.fpt.com.vn}")
    private String einvoiceApiUrl;

    @Value("${fpt.einvoice.api.delete.url:/delete-icr}")
    private String deleteUrl;

    @Value("${fpt.einvoice.api.cancel.url:/cancel-icr}")
    private String cancelUrl;

    @Value("${fpt.einvoice.api.replace.url:/replace-icr}")
    private String replaceUrl;

    @Value("${fpt.einvoice.api.search.url:/search-icr}")
    private String searchUrl;

    @Value("${fpt.einvoice.api.create.url:/create-icr}")
    private String createUrl;

    @Value("${fpt.einvoice.api.update.url:/update-icr}")
    private String updateUrl;

    @Value("${fpt.einvoice.api.username:}")
    private String apiUsername;

    @Value("${fpt.einvoice.api.password:}")
    private String apiPassword;

    @Operation(summary = "Xóa hóa đơn chưa phát hành")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa hóa đơn thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu request không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/delete-icr")
    public ResponseEntity<?> deleteInvoice(@RequestBody DeleteInvoiceRequest request) {
        try {
            log.info("Nhận yêu cầu xóa hóa đơn - INC: {}, Username: {}", 
                    request.getRefInv().getInc(), request.getUser().getUsername());
            
            DeleteInvoiceResponse response = callDeleteInvoiceApi(request);
            
            log.info("Xóa hóa đơn hoàn thành - INC: {}, ErrorCode: {}", 
                    request.getRefInv().getInc(), response.getErrorCode());
            
            return ResponseHelper.ok(response);
            
        } catch (Exception ex) {
            log.error("Lỗi khi xóa hóa đơn - INC: {}", 
                    request.getRefInv() != null ? request.getRefInv().getInc() : "unknown", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Hủy hóa đơn")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hủy hóa đơn thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu request không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/cancel-icr")
    public ResponseEntity<?> cancelInvoice(@RequestBody CancelInvoiceRequest request) {
        try {
            log.info("Nhận yêu cầu hủy hóa đơn - Username: {}, STAX: {}, Số items: {}", 
                    request.getUser().getUsername(), 
                    request.getWrongnotice().getStax(),
                    request.getWrongnotice().getItems().size());
            
            CancelInvoiceResponse response = callCancelInvoiceApi(request);
            
            log.info("Hủy hóa đơn hoàn thành - ErrorCode: {}", response.getErrorCode());
            
            return ResponseHelper.ok(response);
            
        } catch (Exception ex) {
            log.error("Lỗi khi hủy hóa đơn - Username: {}", 
                    request.getUser() != null ? request.getUser().getUsername() : "unknown", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Thay thế hóa đơn")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thay thế hóa đơn thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu request không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/replace-icr")
    public ResponseEntity<?> replaceInvoice(@RequestBody ReplaceInvoiceRequest request) {
        try {
            log.info("Nhận yêu cầu thay thế hóa đơn - Username: {}, STAX: {}, Số items: {}", 
                    request.getUser().getUsername(), 
                    request.getInv().getStax(),
                    request.getInv().getItems().size());
            
            ReplaceInvoiceResponse response = callReplaceInvoiceApi(request);
            
            log.info("Thay thế hóa đơn hoàn thành - ErrorCode: {}", response.getErrorCode());
            
            return ResponseHelper.ok(response);
            
        } catch (Exception ex) {
            log.error("Lỗi khi thay thế hóa đơn - Username: {}", 
                    request.getUser() != null ? request.getUser().getUsername() : "unknown", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tìm kiếm hóa đơn")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm kiếm hóa đơn thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu request không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/search-icr")
    public ResponseEntity<?> searchInvoice(@RequestBody SearchInvoiceRequest request) {
        try {
            log.info("Nhận yêu cầu tìm kiếm hóa đơn - STAX: {}, Type: {}, SID: {}, Username: {}, ToKhaiId: {}", 
                    request.getStax(), request.getType(), request.getSid(), request.getUser().getUsername(), request.getToKhaiId());
            
            String response = callSearchInvoiceApi(request);
            
            log.info("Tìm kiếm hóa đơn hoàn thành - Response: {}", response);
            
            // Xử lý base64 response và lưu vào StoKhai nếu có toKhaiId
            String processedBase64 = null;
            if (request.getToKhaiId() != null) {
                try {
                    processedBase64 = processBase64ResponseAndSave(response, request.getToKhaiId());
                } catch (Exception e) {
                    log.error("Lỗi khi xử lý base64 response và lưu vào tờ khai ID {}: ", request.getToKhaiId(), e);
                    // Không throw exception để không ảnh hưởng đến response chính
                }
            }
            
            // Tạo response cho frontend với base64 và mã 0000
            String frontendResponse = createFrontendResponse(processedBase64);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(frontendResponse);
            
        } catch (Exception ex) {
            log.error("Lỗi khi tìm kiếm hóa đơn - STAX: {}", 
                    request.getStax() != null ? request.getStax() : "unknown", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật trạng thái phát hành", 
               description = "Cập nhật trạng thái phát hành của tờ khai thông tin sang '02'. Request body chỉ cần truyền id của tờ khai.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái phát hành thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu request không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/update-trang-thai-phat-hanh")
    public ResponseEntity<?> updateTrangThaiPhatHanh(@RequestBody UpdateTrangThaiPhatHanhRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật trạng thái phát hành cho tờ khai ID: {}", request.getId());
            
            // Cập nhật trạng thái phát hành sang "02"
            updateTrangThaiPhatHanhTo02(request.getId());
            
            log.info("Đã cập nhật trạng thái phát hành thành '02' cho tờ khai ID: {}", request.getId());
            
            // Gửi email PDF biên lai bất đồng bộ (không đợi kết quả)
            // sendEmailBienLaiAsync(request.getId());
            
            return ResponseHelper.ok("Cập nhật trạng thái phát hành thành công");
            
        } catch (Exception ex) {
            log.error("Lỗi khi cập nhật trạng thái phát hành cho ID {}: ", request.getId(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo ICR e-invoice")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo ICR thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu request không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create-icr")
    public ResponseEntity<?> createIcr(@RequestBody CreateIcrRequest request) {
        try {
            log.info("Nhận yêu cầu tạo ICR e-invoice, toKhaiId: {}", request.getToKhaiId());
            
            String fptResponse = callCreateIcrApi(request);
            
            log.info("Tạo ICR hoàn thành - Response: {}", fptResponse);
            
            // Kiểm tra response và cập nhật trạng thái phát hành nếu thành công
            if (request.getToKhaiId() != null) {
                try {
                    updateTrangThaiPhatHanhIfSuccess(fptResponse, request.getToKhaiId());
                } catch (Exception e) {
                    log.error("Lỗi khi cập nhật trạng thái phát hành cho tờ khai ID {}: ", request.getToKhaiId(), e);
                    // Không throw exception để không ảnh hưởng đến response chính
                }
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fptResponse);
                    
        } catch (Exception ex) {
            log.error("Lỗi khi tạo ICR e-invoice: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Cập nhật ICR e-invoice")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật ICR thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu request không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/update-icr")
    public ResponseEntity<?> updateIcr(@RequestBody CreateIcrRequest request) {
        try {
            log.info("Nhận yêu cầu cập nhật ICR e-invoice");
            
            String fptResponse = callUpdateIcrApi(request);
            
            log.info("Cập nhật ICR hoàn thành - Response: {}", fptResponse);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fptResponse);
                    
        } catch (Exception ex) {
            log.error("Lỗi khi cập nhật ICR e-invoice: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    // Private methods for API calls
    private HttpHeaders createBasicAuthHeaders() {
        return createBasicAuthHeaders(apiUsername, apiPassword);
    }
    
    private HttpHeaders createBasicAuthHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        
        String authUsername = username != null ? username : apiUsername;
        String authPassword = password != null ? password : apiPassword;
        
        if (authUsername != null && !authUsername.isEmpty() && authPassword != null && !authPassword.isEmpty()) {
            String auth = authUsername + ":" + authPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
            log.info("Sử dụng Basic Authentication với username: {}", authUsername);
        }
        
        return headers;
    }

    private DeleteInvoiceResponse callDeleteInvoiceApi(DeleteInvoiceRequest request) throws BusinessException {
        try {
            HttpHeaders headers = createBasicAuthHeaders();

            String requestBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = einvoiceApiUrl + deleteUrl;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            return objectMapper.readValue(response.getBody(), DeleteInvoiceResponse.class);
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API xóa hóa đơn: " + e.getMessage());
        }
    }

    private CancelInvoiceResponse callCancelInvoiceApi(CancelInvoiceRequest request) throws BusinessException {
        try {
            HttpHeaders headers = createBasicAuthHeaders();

            String requestBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = einvoiceApiUrl + cancelUrl;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            return objectMapper.readValue(response.getBody(), CancelInvoiceResponse.class);
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API hủy hóa đơn: " + e.getMessage());
        }
    }

    private ReplaceInvoiceResponse callReplaceInvoiceApi(ReplaceInvoiceRequest request) throws BusinessException {
        try {
            HttpHeaders headers = createBasicAuthHeaders();

            String requestBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = einvoiceApiUrl + replaceUrl;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            return objectMapper.readValue(response.getBody(), ReplaceInvoiceResponse.class);
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API thay thế hóa đơn: " + e.getMessage());
        }
    }

    private String callSearchInvoiceApi(SearchInvoiceRequest request) throws BusinessException {
        try {
            HttpHeaders headers = createBasicAuthHeaders(request.getUser().getUsername(), request.getUser().getPassword());
            
            // Không cần request body, chỉ cần Basic Auth
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = UriComponentsBuilder.fromHttpUrl(einvoiceApiUrl + searchUrl)
                    .queryParam("stax", request.getStax())
                    .queryParam("type", request.getType())
                    .queryParam("sid", request.getSid())
                    .toUriString();

            log.info("Gọi FPT API Search: {}", url);
            
            // Thử GET method trước (vì search thường dùng GET)
            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                log.info("Thành công với GET method");
            } catch (Exception getException) {
                log.warn("GET method thất bại, thử POST method: {}", getException.getMessage());
                response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                log.info("Thành công với POST method");
            }

            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API tìm kiếm hóa đơn: " + e.getMessage());
        }
    }


    private String callCreateIcrApi(CreateIcrRequest request) throws BusinessException {
        try {
            HttpHeaders headers = createBasicAuthHeaders();

            // Tạo request body không bao gồm toKhaiId (chỉ gửi user và receipt sang FPT)
            java.util.Map<String, Object> fptRequest = new java.util.HashMap<>();
            fptRequest.put("user", request.getUser());
            fptRequest.put("receipt", request.getReceipt());
            
            String requestBody = objectMapper.writeValueAsString(fptRequest);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = einvoiceApiUrl + createUrl;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API tạo ICR: " + e.getMessage());
        }
    }

    private String callUpdateIcrApi(CreateIcrRequest request) throws BusinessException {
        try {
            HttpHeaders headers = createBasicAuthHeaders();

            // Tạo request body không bao gồm toKhaiId (chỉ gửi user và receipt sang FPT)
            java.util.Map<String, Object> fptRequest = new java.util.HashMap<>();
            fptRequest.put("user", request.getUser());
            fptRequest.put("receipt", request.getReceipt());
            
            String requestBody = objectMapper.writeValueAsString(fptRequest);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = einvoiceApiUrl + updateUrl;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API cập nhật ICR: " + e.getMessage());
        }
    }

    /**
     * Cập nhật trạng thái phát hành về "01" và lưu idPhatHanh nếu FPT response thành công (status = 6)
     */
    private void updateTrangThaiPhatHanhIfSuccess(String fptResponse, Long toKhaiId) {
        try {
            // Parse JSON response để lấy status và sid
            ObjectMapper mapper = new ObjectMapper();
            Object responseObj = mapper.readValue(fptResponse, Object.class);
            
            // Kiểm tra status = 6 (thành công)
            if (responseObj instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> responseMap = (java.util.Map<String, Object>) responseObj;
                Object statusObj = responseMap.get("status");
                Object sidObj = responseMap.get("sid");
                Object secObj = responseMap.get("sec");
                Object seqObj = responseMap.get("seq");
                Object serialObj = responseMap.get("serial");
                Object formObj = responseMap.get("form");
                Object idtObj = responseMap.get("idt");
                
                if (statusObj != null) {
                    int status = Integer.parseInt(statusObj.toString());
                    
                    if (status == 3) {
                        log.info("FPT response thành công (status=3), cập nhật trạng thái phát hành cho tờ khai ID: {}", toKhaiId);
                        
                        // Lấy sid từ response
                        String sid = sidObj != null ? sidObj.toString() : null;
                        if (sid != null) {
                            log.info("Lấy được sid từ FPT response: {} cho tờ khai ID: {}", sid, toKhaiId);
                        } else {
                            log.warn("Không tìm thấy field 'sid' trong FPT response cho tờ khai ID: {}", toKhaiId);
                        }
                        
                        // Cập nhật trạng thái phát hành về "01" và các thông tin trả về
                        updateStoKhaiAfterSuccess(toKhaiId, sid, secObj, seqObj, serialObj, formObj, idtObj);
                        
                        log.info("Đã cập nhật trạng thái phát hành thành '01' và idPhatHanh cho tờ khai ID: {}", toKhaiId);
                    } else {
                        log.info("FPT response không thành công (status={}), không cập nhật trạng thái phát hành cho tờ khai ID: {}", 
                                status, toKhaiId);
                    }
                } else {
                    log.warn("Không tìm thấy field 'status' trong FPT response cho tờ khai ID: {}", toKhaiId);
                }
            } else {
                log.warn("FPT response không phải là JSON object cho tờ khai ID: {}", toKhaiId);
            }
        } catch (Exception e) {
            log.error("Lỗi khi parse FPT response hoặc cập nhật trạng thái phát hành cho tờ khai ID {}: ", toKhaiId, e);
            throw new RuntimeException("Lỗi khi xử lý FPT response: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật tờ khai thông tin sau khi FPT response thành công
     */
    private void updateStoKhaiAfterSuccess(Long toKhaiId, String sid) {
        updateStoKhaiAfterSuccess(toKhaiId, sid, null, null, null, null, null);
    }

    private void updateStoKhaiAfterSuccess(Long toKhaiId, String sid, Object secObj, Object seqObj, Object serialObj, Object formObj, Object idtObj) {
        try {
            // Lấy tờ khai hiện tại
            com.pht.entity.StoKhai toKhai = toKhaiThongTinService.getToKhaiThongTinById(toKhaiId);
            
            // Cập nhật trạng thái phát hành và idPhatHanh
            toKhai.setTrangThaiPhatHanh("01");
            if (sid != null) {
                toKhai.setIdPhatHanh(sid);
            }

            // Cập nhật các field liên quan từ response
            if (secObj != null) {
                toKhai.setMaTraCuuBienLai(secObj.toString());
            }
            if (seqObj != null) {
                toKhai.setSoBienLai(seqObj.toString());
            }
            if (serialObj != null && seqObj != null) {
                toKhai.setKyHieuBienLai(serialObj.toString() +"_"+ seqObj.toString());
            }
            if (formObj != null) {
                toKhai.setMauBienLai(formObj.toString());
            }
            if (idtObj != null) {
                try {
                    // idt ví dụ: "2025-09-23 10:23:13" -> lấy phần ngày
                    String idt = idtObj.toString();
                    String datePart = idt.length() >= 10 ? idt.substring(0, 10) : idt;
                    java.time.LocalDate parsed = java.time.LocalDate.parse(datePart);
                    toKhai.setNgayBienLai(parsed);
                } catch (Exception ignore) {
                    // Bỏ qua nếu parse lỗi
                }
            }
            
            // Lưu vào database
            toKhaiThongTinService.save(toKhai);
            
            // Cập nhật biên lai liên kết nếu có
            if (toKhai.getIdBienLai() != null) {
                updateBienLaiFromFptResponse(toKhai.getIdBienLai(), secObj, seqObj, serialObj, formObj, idtObj, sid);
            }
            
            log.info("Đã cập nhật tờ khai ID: {} - trangThaiPhatHanh: 01, idPhatHanh: {}", toKhaiId, sid);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật tờ khai thông tin ID {}: ", toKhaiId, e);
            throw new RuntimeException("Lỗi khi cập nhật tờ khai thông tin: " + e.getMessage(), e);
        }
    }


    /**
     * Cập nhật biên lai từ FPT response
     */
    private void updateBienLaiFromFptResponse(Long bienLaiId, Object secObj, Object seqObj, Object serialObj, Object formObj, Object idtObj, String sid) {
        try {
            log.info("Cập nhật biên lai ID: {} từ FPT response", bienLaiId);
            
            // Lấy biên lai hiện tại
            com.pht.entity.SBienLai bienLai = sBienLaiService.getBienLaiById(bienLaiId);
            
            // Cập nhật các thông tin từ FPT response
            if (secObj != null) {
                // Có thể lưu vào ghiChu hoặc tạo field mới nếu cần
                bienLai.setGhiChu("FPT SEC: " + secObj.toString());
            }
            if (seqObj != null) {
                bienLai.setSoBl(seqObj.toString());
            }
            if (serialObj != null && seqObj != null) {
                bienLai.setMaBl(serialObj.toString() + "_" + seqObj.toString());
            }
            if (formObj != null) {
                // Có thể lưu vào loaiCtiet hoặc tạo field mới nếu cần
                bienLai.setLoaiCtiet("FPT FORM: " + formObj.toString());
            }
            if (idtObj != null) {
                try {
                    String idt = idtObj.toString();
                    String datePart = idt.length() >= 10 ? idt.substring(0, 10) : idt;
                    java.time.LocalDate parsed = java.time.LocalDate.parse(datePart);
                    bienLai.setNgayBl(parsed.atStartOfDay());
                } catch (Exception ignore) {
                    // Bỏ qua nếu parse lỗi
                }
            }
            if (sid != null) {
                bienLai.setIdPhatHanh(sid);
            }
            
            // Lưu biên lai
            sBienLaiService.save(bienLai);
            
            log.info("Đã cập nhật biên lai ID: {} với thông tin từ FPT response", bienLaiId);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật biên lai ID {} từ FPT response: ", bienLaiId, e);
            // Không throw exception để không ảnh hưởng đến luồng chính
        }
    }

    /**
     * Xử lý base64 response từ FPT và lưu vào StoKhai
     * @return base64 data đã được xử lý (cắt sau 'base64,')
     */
    private String processBase64ResponseAndSave(String fptResponse, Long toKhaiId) {
        try {
            log.info("Bắt đầu xử lý base64 response cho tờ khai ID: {}", toKhaiId);
            
            // Parse JSON response để lấy base64 data
            ObjectMapper mapper = new ObjectMapper();
            Object responseObj = mapper.readValue(fptResponse, Object.class);
            
            // Tìm base64 data trong response (có thể là array hoặc object)
            String base64Data = extractBase64Data(responseObj);
            
            if (base64Data != null && !base64Data.isEmpty()) {
                log.info("Tìm thấy base64 data, bắt đầu cắt dữ liệu sau 'base64,' cho tờ khai ID: {}", toKhaiId);
                
                // Cắt dữ liệu sau 'base64,'
                String processedBase64 = processBase64String(base64Data);
                
                // Lưu vào StoKhai
                saveBase64ToStoKhai(toKhaiId, processedBase64);
                
                // Cập nhật imageBl cho SBienLai từ StoKhai thông qua ID_BIEN_LAI
                updateBienLaiImageBlFromStoKhai(toKhaiId, processedBase64);
                
                log.info("Đã lưu base64 data cho tờ khai ID: {}", toKhaiId);
                
                return processedBase64;
            } else {
                log.warn("Không tìm thấy base64 data trong response cho tờ khai ID: {}", toKhaiId);
                return null;
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý base64 response cho tờ khai ID {}: ", toKhaiId, e);
            throw new RuntimeException("Lỗi khi xử lý base64 response: " + e.getMessage(), e);
        }
    }

    /**
     * Trích xuất base64 data từ response (có thể là array hoặc object)
     */
    private String extractBase64Data(Object responseObj) {
        try {
            // Nếu response là array
            if (responseObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> responseList = (java.util.List<Object>) responseObj;
                
                for (Object item : responseList) {
                    if (item instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> itemMap = (java.util.Map<String, Object>) item;
                        
                        // Tìm kiếm trong field "pdf"
                        Object pdfValue = itemMap.get("pdf");
                        if (pdfValue != null) {
                            String pdfString = pdfValue.toString();
                            if (pdfString.contains("base64,")) {
                                log.info("Tìm thấy base64 data trong field 'pdf' của array item");
                                return pdfString;
                            }
                        }
                        
                        // Tìm kiếm trong các field khác
                        String result = searchBase64InMap(itemMap);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
            // Nếu response là object
            else if (responseObj instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> responseMap = (java.util.Map<String, Object>) responseObj;
                
                // Tìm kiếm base64 data trong các field có thể có
                String[] possibleFields = {"pdf", "data", "content", "base64", "file", "document"};
                
                for (String field : possibleFields) {
                    Object value = responseMap.get(field);
                    if (value != null) {
                        String stringValue = value.toString();
                        if (stringValue.contains("base64,")) {
                            log.info("Tìm thấy base64 data trong field: {}", field);
                            return stringValue;
                        }
                    }
                }
                
                // Nếu không tìm thấy trong các field cụ thể, tìm kiếm trong toàn bộ response
                return searchBase64InMap(responseMap);
            }
            
            return null;
        } catch (Exception e) {
            log.error("Lỗi khi trích xuất base64 data: ", e);
            return null;
        }
    }

    /**
     * Tìm kiếm base64 data trong map một cách đệ quy
     */
    private String searchBase64InMap(java.util.Map<String, Object> map) {
        for (java.util.Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String stringValue = (String) value;
                if (stringValue.contains("base64,")) {
                    log.info("Tìm thấy base64 data trong field: {}", entry.getKey());
                    return stringValue;
                }
            } else if (value instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                String result = searchBase64InMap((java.util.Map<String, Object>) value);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> list = (java.util.List<Object>) value;
                for (Object item : list) {
                    if (item instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        String result = searchBase64InMap((java.util.Map<String, Object>) item);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Xử lý chuỗi base64, cắt dữ liệu sau 'base64,'
     */
    private String processBase64String(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        
        // Tìm vị trí của 'base64,'
        int base64Index = base64String.indexOf("base64,");
        if (base64Index != -1) {
            // Cắt dữ liệu sau 'base64,'
            String processedData = base64String.substring(base64Index + 7); // 7 là độ dài của "base64,"
            log.info("Đã cắt dữ liệu base64, độ dài sau khi cắt: {}", processedData.length());
            return processedData;
        } else {
            log.warn("Không tìm thấy 'base64,' trong chuỗi, trả về nguyên bản");
            return base64String;
        }
    }

    /**
     * Lưu base64 data vào StoKhai
     */
    private void saveBase64ToStoKhai(Long toKhaiId, String base64Data) {
        try {
            // Lấy tờ khai hiện tại
            com.pht.entity.StoKhai toKhai = toKhaiThongTinService.getToKhaiThongTinById(toKhaiId);
            
            // Cập nhật imageBl field
            toKhai.setImageBl(base64Data);
            
            // Lưu vào database
            toKhaiThongTinService.save(toKhai);
            
            log.info("Đã lưu base64 data vào imageBl field cho tờ khai ID: {}, độ dài data: {}", 
                    toKhaiId, base64Data.length());
        } catch (Exception e) {
            log.error("Lỗi khi lưu base64 data cho tờ khai ID {}: ", toKhaiId, e);
            throw new RuntimeException("Lỗi khi lưu base64 data: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật trạng thái phát hành sang "02"
     */
    private void updateTrangThaiPhatHanhTo02(Long toKhaiId) {
        try {
            // Lấy tờ khai hiện tại
            com.pht.entity.StoKhai toKhai = toKhaiThongTinService.getToKhaiThongTinById(toKhaiId);
            
            // Cập nhật trạng thái phát hành sang "02"
            toKhai.setTrangThaiPhatHanh("02");
            
            // Lưu vào database
            toKhaiThongTinService.save(toKhai);
            
            log.info("Đã cập nhật trangThaiPhatHanh = '02' cho tờ khai ID: {}", toKhaiId);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái phát hành cho tờ khai ID {}: ", toKhaiId, e);
            throw new RuntimeException("Lỗi khi cập nhật trạng thái phát hành: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật imageBl của SBienLai từ StoKhai thông qua ID_BIEN_LAI
     */
    private void updateBienLaiImageBlFromStoKhai(Long toKhaiId, String base64Data) {
        try {
            // Lấy tờ khai để lấy ID_BIEN_LAI
            com.pht.entity.StoKhai toKhai = toKhaiThongTinService.getToKhaiThongTinById(toKhaiId);
            
            if (toKhai.getIdBienLai() != null) {
                log.info("Tìm thấy ID_BIEN_LAI: {} trong tờ khai ID: {}, bắt đầu cập nhật imageBl cho biên lai", 
                        toKhai.getIdBienLai(), toKhaiId);
                
                // Lấy biên lai từ ID_BIEN_LAI
                com.pht.entity.SBienLai bienLai = sBienLaiService.findById(toKhai.getIdBienLai());
                
                if (bienLai != null) {
                    // Cập nhật imageBl cho biên lai
                    bienLai.setImageBl(base64Data);
                    
                    // Lưu vào database
                    sBienLaiService.save(bienLai);
                    
                    log.info("Đã cập nhật imageBl cho biên lai ID: {} từ tờ khai ID: {}, độ dài data: {}", 
                            toKhai.getIdBienLai(), toKhaiId, base64Data.length());
                } else {
                    log.warn("Không tìm thấy biên lai với ID: {} từ tờ khai ID: {}", 
                            toKhai.getIdBienLai(), toKhaiId);
                }
            } else {
                log.info("Tờ khai ID: {} không có ID_BIEN_LAI, bỏ qua cập nhật imageBl cho biên lai", toKhaiId);
            }
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật imageBl cho biên lai từ tờ khai ID {}: ", toKhaiId, e);
            // Không throw exception để không ảnh hưởng đến flow chính
        }
    }

    /**
     * Gửi email PDF biên lai cho tờ khai bất đồng bộ (không đợi kết quả)
     */
    @SuppressWarnings("unused")
    private void sendEmailBienLaiAsync(Long toKhaiId) {
        try {
            log.info("🚀 Bắt đầu gửi email bất đồng bộ cho tờ khai ID: {}", toKhaiId);
            
            // Lấy tờ khai để lấy ID_BIEN_LAI
            com.pht.entity.StoKhai toKhai = toKhaiThongTinService.getToKhaiThongTinById(toKhaiId);
            
            if (toKhai.getIdBienLai() == null) {
                log.info("Tờ khai ID: {} không có ID_BIEN_LAI, bỏ qua gửi email", toKhaiId);
                return;
            }
            
            // Lấy biên lai từ ID_BIEN_LAI
            com.pht.entity.SBienLai bienLai = sBienLaiService.findById(toKhai.getIdBienLai());
            
            if (bienLai == null) {
                log.warn("Không tìm thấy biên lai với ID: {} từ tờ khai ID: {}", 
                        toKhai.getIdBienLai(), toKhaiId);
                return;
            }
            
            // Kiểm tra email và imageBl
            if (bienLai.getEmail() == null || bienLai.getEmail().trim().isEmpty()) {
                log.warn("Biên lai ID: {} không có email, bỏ qua gửi email", bienLai.getId());
                return;
            }
            
            if (bienLai.getImageBl() == null || bienLai.getImageBl().trim().isEmpty()) {
                log.warn("Biên lai ID: {} không có imageBl, bỏ qua gửi email", bienLai.getId());
                return;
            }
            
            // Xử lý base64 data
            byte[] pdfBytes;
            try {
                String cleanBase64 = bienLai.getImageBl();
                if (cleanBase64.contains(",")) {
                    cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
                }
                
                byte[] base64Bytes = java.util.Base64.getDecoder().decode(cleanBase64);
                
                if (isPdfData(base64Bytes)) {
                    pdfBytes = base64Bytes;
                } else {
                    pdfBytes = pdfConverterUtil.convertBase64ImageToPdf(bienLai.getImageBl());
                }
            } catch (Exception e) {
                log.error("Lỗi khi xử lý base64 data cho biên lai ID {}: ", bienLai.getId(), e);
                return;
            }
            
            // Chuẩn bị thông tin email
            String emailSubject = "Biên lai thanh toán - " + (bienLai.getSoBl() != null ? bienLai.getSoBl() : "BL" + bienLai.getId());
            String fileName = "BL_PHT_" + (bienLai.getMaBl() != null ? bienLai.getMaBl() : bienLai.getId()) + ".pdf";
            String htmlContent = createEmailHtmlContent(bienLai, toKhai);
            java.util.List<String> emailList = java.util.Arrays.asList(bienLai.getEmail().trim());
            
            // Gửi email bất đồng bộ - ĐÃ TẮT
            // emailService.sendEmailWithPdfAttachmentAsync(emailList, emailSubject, htmlContent, pdfBytes, fileName);
            
            log.info("📧 Đã khởi tạo gửi email bất đồng bộ cho tờ khai ID: {}, email: {}, API sẽ trả response ngay lập tức", 
                    toKhaiId, bienLai.getEmail());
            
        } catch (Exception e) {
            log.error("❌ Lỗi khi khởi tạo gửi email bất đồng bộ cho tờ khai ID {}: ", toKhaiId, e);
        }
    }

    /**
     * Gửi email PDF biên lai cho tờ khai
     */
    @SuppressWarnings("unused")
    private void sendEmailBienLai(Long toKhaiId) {
        try {
            log.info("Bắt đầu gửi email PDF biên lai cho tờ khai ID: {}", toKhaiId);
            
            // Lấy tờ khai để lấy ID_BIEN_LAI
            com.pht.entity.StoKhai toKhai = toKhaiThongTinService.getToKhaiThongTinById(toKhaiId);
            
            if (toKhai.getIdBienLai() == null) {
                log.info("Tờ khai ID: {} không có ID_BIEN_LAI, bỏ qua gửi email", toKhaiId);
                return;
            }
            
            log.info("Tìm thấy ID_BIEN_LAI: {} trong tờ khai ID: {}, bắt đầu lấy thông tin biên lai", 
                    toKhai.getIdBienLai(), toKhaiId);
            
            // Lấy biên lai từ ID_BIEN_LAI
            com.pht.entity.SBienLai bienLai = sBienLaiService.findById(toKhai.getIdBienLai());
            
            if (bienLai == null) {
                log.warn("Không tìm thấy biên lai với ID: {} từ tờ khai ID: {}", 
                        toKhai.getIdBienLai(), toKhaiId);
                return;
            }
            
            // Kiểm tra email
            if (bienLai.getEmail() == null || bienLai.getEmail().trim().isEmpty()) {
                log.warn("Biên lai ID: {} không có email, bỏ qua gửi email", bienLai.getId());
                return;
            }
            
            // Kiểm tra imageBl (base64)
            if (bienLai.getImageBl() == null || bienLai.getImageBl().trim().isEmpty()) {
                log.warn("Biên lai ID: {} không có imageBl, bỏ qua gửi email", bienLai.getId());
                return;
            }
            
            log.info("Tìm thấy biên lai ID: {}, email: {}, có imageBl (độ dài: {})", 
                    bienLai.getId(), bienLai.getEmail(), bienLai.getImageBl().length());
            
            
            // Xử lý base64 data - có thể là image hoặc PDF
            byte[] pdfBytes;
            try {
                // Clean base64 string
                String cleanBase64 = bienLai.getImageBl();
                if (cleanBase64.contains(",")) {
                    cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
                }
                
                // Decode base64 thành byte array
                byte[] base64Bytes = java.util.Base64.getDecoder().decode(cleanBase64);
                log.info("Decode base64 thành công, kích thước: {} bytes", base64Bytes.length);
                
                // Kiểm tra xem có phải là PDF không
                if (isPdfData(base64Bytes)) {
                    log.info("Base64 data đã là PDF, sử dụng trực tiếp");
                    pdfBytes = base64Bytes;
                } else {
                    log.info("Base64 data là image, convert thành PDF");
                    // Convert image thành PDF
                    pdfBytes = pdfConverterUtil.convertBase64ImageToPdf(bienLai.getImageBl());
                    log.info("Convert base64 image thành PDF thành công, kích thước PDF: {} bytes", pdfBytes.length);
                }
            } catch (Exception e) {
                log.error("Lỗi khi xử lý base64 data cho biên lai ID {}: ", bienLai.getId(), e);
                return;
            }
            
            // Chuẩn bị thông tin email
            String emailSubject = "Biên lai thanh toán - " + (bienLai.getSoBl() != null ? bienLai.getSoBl() : "BL" + bienLai.getId());
            String fileName = "BL_PHT_" + (bienLai.getMaBl() != null ? bienLai.getMaBl() : bienLai.getId()) + ".pdf";
            
            // Tạo nội dung HTML email
            String htmlContent = createEmailHtmlContent(bienLai, toKhai);
            log.info("Tạo nội dung HTML email thành công, độ dài: {} ký tự", htmlContent.length());
            
            // Chuẩn bị danh sách email
            java.util.List<String> emailList = java.util.Arrays.asList(bienLai.getEmail().trim());
            log.info("Chuẩn bị gửi email đến: {}, subject: {}, fileName: {}, PDF size: {} bytes", 
                    emailList, emailSubject, fileName, pdfBytes.length);
            
            // Gửi email - ĐÃ TẮT
            // boolean emailSent = emailService.sendEmailWithPdfAttachment(
            //     emailList, emailSubject, htmlContent, pdfBytes, fileName);
            boolean emailSent = false; // Tạm thời set false vì đã tắt email
            
            if (emailSent) {
                log.info("✅ Gửi email PDF biên lai THÀNH CÔNG cho biên lai ID: {}, email: {}", 
                        bienLai.getId(), bienLai.getEmail());
            } else {
                log.error("❌ Gửi email PDF biên lai THẤT BẠI cho biên lai ID: {}, email: {}", 
                        bienLai.getId(), bienLai.getEmail());
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi gửi email PDF biên lai cho tờ khai ID {}: ", toKhaiId, e);
            // Không throw exception để không ảnh hưởng đến flow chính
        }
    }
    
    /**
     * Kiểm tra xem byte array có phải là PDF data không
     */
    private boolean isPdfData(byte[] data) {
        if (data.length < 4) {
            return false;
        }
        
        // PDF magic bytes: %PDF
        return data[0] == 0x25 && data[1] == 0x50 && data[2] == 0x44 && data[3] == 0x46;
    }


    /**
     * Tạo nội dung HTML cho email
     */
    private String createEmailHtmlContent(com.pht.entity.SBienLai bienLai, com.pht.entity.StoKhai toKhai) {
        StringBuilder html = new StringBuilder();
        
        // Header với logo và tiêu đề
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'></head>");
        html.append("<body style='font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5;'>");
        
        // Container chính
        html.append("<div style='max-width: 600px; margin: 0 auto; background-color: white; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); overflow: hidden;'>");
        
        // Header với màu nền
        html.append("<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;'>");
        html.append("<h1 style='color: white; margin: 0; font-size: 28px; font-weight: bold;'>📄 BIÊN LAI THANH TOÁN</h1>");
        html.append("<p style='color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 16px;'>Hệ thống Phí Hạ Tầng Cảng Biển HCM</p>");
        html.append("</div>");
        
        // Nội dung chính
        html.append("<div style='padding: 30px;'>");
        
        // Thông báo thành công
        html.append("<div style='background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 5px; padding: 15px; margin-bottom: 25px;'>");
        html.append("<p style='margin: 0; color: #155724; font-weight: bold;'>✅ Giao dịch đã được xử lý thành công!</p>");
        html.append("</div>");
        
        // Thông tin biên lai
        html.append("<h2 style='color: #2c3e50; margin-bottom: 20px; border-bottom: 2px solid #3498db; padding-bottom: 10px;'>📋 Thông tin biên lai</h2>");
        
        html.append("<table style='width: 100%; border-collapse: collapse; margin-bottom: 25px;'>");
        
        if (bienLai.getSoBl() != null) {
            html.append("<tr><td style='padding: 12px; border-bottom: 1px solid #eee; font-weight: bold; color: #34495e; width: 40%;'>Mã số biên lai:</td>");
            html.append("<td style='padding: 12px; border-bottom: 1px solid #eee; color: #2c3e50;'>").append(bienLai.getMaBl()).append("</td></tr>");
        }
        
        if (bienLai.getTenDvi() != null) {
            html.append("<tr><td style='padding: 12px; border-bottom: 1px solid #eee; font-weight: bold; color: #34495e;'>Tên đơn vị:</td>");
            html.append("<td style='padding: 12px; border-bottom: 1px solid #eee; color: #2c3e50;'>").append(bienLai.getTenDvi()).append("</td></tr>");
        }
        
        if (bienLai.getMst() != null) {
            html.append("<tr><td style='padding: 12px; border-bottom: 1px solid #eee; font-weight: bold; color: #34495e;'>Mã số thuế:</td>");
            html.append("<td style='padding: 12px; border-bottom: 1px solid #eee; color: #2c3e50;'>").append(bienLai.getMst()).append("</td></tr>");
        }
        
        if (toKhai.getSoToKhai() != null) {
            html.append("<tr><td style='padding: 12px; border-bottom: 1px solid #eee; font-weight: bold; color: #34495e;'>Số tờ khai:</td>");
            html.append("<td style='padding: 12px; border-bottom: 1px solid #eee; color: #2c3e50;'>").append(toKhai.getSoToKhai()).append("</td></tr>");
        }
        
        if (bienLai.getNgayBl() != null) {
            html.append("<tr><td style='padding: 12px; border-bottom: 1px solid #eee; font-weight: bold; color: #34495e;'>Ngày biên lai:</td>");
            html.append("<td style='padding: 12px; border-bottom: 1px solid #eee; color: #2c3e50;'>").append(bienLai.getNgayBl().toString()).append("</td></tr>");
        }
        
        if (bienLai.getDiaChi() != null) {
            html.append("<tr><td style='padding: 12px; border-bottom: 1px solid #eee; font-weight: bold; color: #34495e;'>Địa chỉ:</td>");
            html.append("<td style='padding: 12px; border-bottom: 1px solid #eee; color: #2c3e50;'>").append(bienLai.getDiaChi()).append("</td></tr>");
        }
        
        html.append("</table>");
        
        // Thông tin về file đính kèm
        html.append("<div style='background-color: #e3f2fd; border-left: 4px solid #2196f3; padding: 15px; margin: 20px 0;'>");
        html.append("<h3 style='margin: 0 0 10px 0; color: #1976d2;'>📎 File đính kèm</h3>");
        html.append("<p style='margin: 0; color: #424242;'>Biên lai PDF đã được đính kèm trong email này. Vui lòng tải về và lưu trữ để làm bằng chứng thanh toán.</p>");
        html.append("</div>");
        
        // Lưu ý quan trọng
        html.append("<div style='background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0;'>");
        html.append("<h4 style='margin: 0 0 10px 0; color: #856404;'>⚠️ Lưu ý quan trọng</h4>");
        html.append("<ul style='margin: 0; padding-left: 20px; color: #856404;'>");
        html.append("<li>Biên lai này là bằng chứng hợp lệ của giao dịch</li>");
        html.append("<li>Vui lòng lưu trữ cẩn thận để sử dụng khi cần thiết</li>");
        html.append("<li>Liên hệ hotline nếu có thắc mắc về giao dịch</li>");
        html.append("</ul>");
        html.append("</div>");
        
        html.append("</div>");
        
        // Footer
        html.append("<div style='background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #dee2e6;'>");
        html.append("<p style='margin: 0; color: #6c757d; font-size: 14px;'>Trân trọng,<br><strong>Hệ thống Phí Hạ Tầng Cảng Biển HCM</strong></p>");
        html.append("<p style='margin: 10px 0 0 0; color: #adb5bd; font-size: 12px;'>Email được gửi tự động từ hệ thống</p>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</body></html>");
        
        return html.toString();
    }

    /**
     * Tạo response cho frontend với base64 và mã 0000
     */
    private String createFrontendResponse(String processedBase64) {
        try {
            log.info("Bắt đầu tạo response cho frontend - processedBase64: {}", 
                    processedBase64 != null ? "có data, độ dài: " + processedBase64.length() : "null");
            
            // Tạo response object cho frontend
            java.util.Map<String, Object> frontendResponse = new java.util.HashMap<>();
            
            // Thêm mã 0000 (thành công)
            frontendResponse.put("errorCode", "0000");
            frontendResponse.put("errorMessage", "Success");
            
            // Thêm base64 data nếu có
            if (processedBase64 != null && !processedBase64.isEmpty()) {
                frontendResponse.put("base64Data", processedBase64);
                log.info("Đã thêm base64 data vào response cho frontend, độ dài: {}", processedBase64.length());
            } else {
                frontendResponse.put("base64Data", null);
                log.info("Không có base64 data để trả về cho frontend");
            }
            
            // Convert thành JSON string
            String result = objectMapper.writeValueAsString(frontendResponse);
            log.info("Đã tạo response cho frontend thành công: {}", result);
            
            return result;
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo response cho frontend: ", e);
            // Fallback: trả về response đơn giản
            return "{\"errorCode\":\"0000\",\"errorMessage\":\"Success\",\"base64Data\":null}";
        }
    }

}
