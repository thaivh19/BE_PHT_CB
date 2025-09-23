package com.pht.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pht.common.OrderBy;
import com.pht.common.helper.ResponseHelper;
import com.pht.common.model.ApiDataResponse;
import com.pht.model.request.ImportCertificateRequest;
import com.pht.model.request.ImportCertificateFileRequest;
import com.pht.model.request.SaveWindowsCertificateRequest;
import com.pht.model.request.SaveCertificateBySerialRequest;
import com.pht.model.request.ClientCertificateListRequest;
import com.pht.model.request.SaveCertificateFromFrontendRequest;
import com.pht.model.request.XmlGenerationRequest;
import com.pht.model.request.PfxSignRequest;
import com.pht.model.response.ChuKySoResponse;
import com.pht.model.response.ImportCertificateResponse;
import com.pht.entity.ChukySo;
import com.pht.repository.ChukySoRepository;
import com.pht.service.CertificateImportService;
import com.pht.service.CertificateFileImportService;
import com.pht.service.XmlGenerationService;
import com.pht.service.WindowsCertificateService;
import com.pht.service.WindowsCertificateSaveService;
import com.pht.service.SimpleCertificateSaveService;
import com.pht.service.ClientCertificateService;
import com.pht.service.FrontendCertificateSaveService;
import com.pht.service.PfxCertificateService;

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
@RequestMapping("/api/chu-ky-so")
@Tag(name = "Chữ ký số", description = "API quản lý chữ ký số")
public class ChuKySoController {

    private final XmlGenerationService xmlGenerationService;
    private final CertificateImportService certificateImportService;
    private final CertificateFileImportService certificateFileImportService;
    private final ChukySoRepository chukySoRepository;
    private final WindowsCertificateService windowsCertificateService;
    private final WindowsCertificateSaveService windowsCertificateSaveService;
    private final SimpleCertificateSaveService simpleCertificateSaveService;
    private final ClientCertificateService clientCertificateService;
    private final FrontendCertificateSaveService frontendCertificateSaveService;
    private final PfxCertificateService pfxCertificateService;

    @Operation(summary = "Lấy danh sách chữ ký số từ database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/danh-sach")
    public ResponseEntity<?> layDanhSachChuKySo() {
        try {
            log.info("Nhận yêu cầu lấy danh sách chữ ký số từ database");
            List<ChukySo> entities = chukySoRepository.findActiveCertificates();
            
            // Convert entity sang response với thông tin như trong hình
            List<ChuKySoResponse> result = entities.stream()
                .map(entity -> {
                    ChuKySoResponse response = new ChuKySoResponse();
                    response.setSerialNumber(entity.getSerialNumber());
                    response.setIssuer(entity.getIssuer());
                    response.setSubject(entity.getSubject());
                    response.setCert(entity.getCertificateData());
                    response.setValidFrom(formatDate(entity.getValidFrom()));
                    response.setValidTo(formatDate(entity.getValidTo()));
                    return response;
                })
                .toList();
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách chữ ký số từ database: ", ex);
            return ResponseHelper.error(ex);
        }
    }
    
    /**
     * Extract tên nhà phát hành từ issuer string
     */
    @SuppressWarnings("unused")
    private String extractIssuerName(String issuer) {
        if (issuer == null || issuer.isEmpty()) {
            return "Unknown";
        }
        
        try {
            // Tìm CN= trong issuer string
            String[] parts = issuer.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("CN=")) {
                    return part.substring(3).trim();
                }
            }
        } catch (Exception e) {
            log.debug("Không thể parse issuer: {}", issuer, e);
        }
        
        return issuer;
    }
    
    /**
     * Format date thành dd/MM/yyyy
     */
    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Operation(summary = "Lấy danh sách chữ ký số từ Windows Security")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/windows-security")
    public ResponseEntity<?> layDanhSachChuKySoTuWindowsSecurity() {
        try {
            log.info("Nhận yêu cầu lấy danh sách chữ ký số từ Windows Security");
            List<ChuKySoResponse> result = windowsCertificateService.getAllWindowsCertificates();
            
            log.info("Lấy thành công {} chữ ký số từ Windows Security", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách chữ ký số từ Windows Security: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy danh sách chữ ký số hợp lệ từ Windows Security")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/windows-security/valid")
    public ResponseEntity<?> layDanhSachChuKySoHopLeTuWindowsSecurity() {
        try {
            log.info("Nhận yêu cầu lấy danh sách chữ ký số hợp lệ từ Windows Security");
            List<ChuKySoResponse> result = windowsCertificateService.getValidWindowsCertificates();
            
            log.info("Lấy thành công {} chữ ký số hợp lệ từ Windows Security", result.size());
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách chữ ký số hợp lệ từ Windows Security: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lấy chữ ký số theo serial number từ Windows Security")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/windows-security/serial/{serialNumber}")
    public ResponseEntity<?> layChuKySoTuWindowsSecurity(@PathVariable String serialNumber) {
        try {
            log.info("Nhận yêu cầu lấy chữ ký số với serial number: {} từ Windows Security", serialNumber);
            ChuKySoResponse result = windowsCertificateService.getWindowsCertificateBySerialNumber(serialNumber);
            
            if (result == null) {
                log.warn("Không tìm thấy chữ ký số với serial number: {} từ Windows Security", serialNumber);
                return ResponseHelper.notFound("Không tìm thấy chữ ký số với serial number: " + serialNumber);
            }
            
            log.info("Lấy thành công chữ ký số với serial number: {} từ Windows Security", serialNumber);
            
            return ResponseHelper.ok(result);
        } catch (Exception ex) {
            log.error("Lỗi khi lấy chữ ký số với serial number: {} từ Windows Security: ", serialNumber, ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lưu chữ ký số từ Windows Security vào database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lưu thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chữ ký số trong Windows Security", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "409", description = "Chữ ký số đã tồn tại trong database", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/windows-security/save")
    public ResponseEntity<?> luuChuKySoTuWindowsSecurity(@RequestBody SaveWindowsCertificateRequest request) {
        try {
            log.info("Nhận yêu cầu lưu chữ ký số từ Windows Security với serial number: {}", request.getSerialNumber());
            
            ChuKySoResponse result = windowsCertificateSaveService.saveWindowsCertificateToDatabase(request);
            
            log.info("Lưu thành công chữ ký số với serial number: {} từ Windows Security", request.getSerialNumber());
            
            return ResponseHelper.ok(result);
            
        } catch (Exception ex) {
            log.error("Lỗi khi lưu chữ ký số từ Windows Security với serial number {}: ", request.getSerialNumber(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lưu chữ ký số từ Windows Security chỉ với SerialNumber")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lưu thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chữ ký số trong Windows Security", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "409", description = "Chữ ký số đã tồn tại trong database", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/save-by-serial")
    public ResponseEntity<?> luuChuKySoBangSerialNumber(@RequestBody SaveCertificateBySerialRequest request) {
        try {
            log.info("Nhận yêu cầu lưu chữ ký số từ Windows Security với serial number: {}", request.getSerialNumber());
            
            ChuKySoResponse result = simpleCertificateSaveService.saveCertificateBySerialNumber(request);
            
            log.info("Lưu thành công chữ ký số với serial number: {} từ Windows Security", request.getSerialNumber());
            
            return ResponseHelper.ok(result);
            
        } catch (Exception ex) {
            log.error("Lỗi khi lưu chữ ký số từ Windows Security với serial number {}: ", request.getSerialNumber(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Nhận danh sách chữ ký số từ frontend React")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lưu thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/client-certificates")
    public ResponseEntity<?> nhanDanhSachChuKySoTuFrontend(@RequestBody ClientCertificateListRequest request) {
        try {
            log.info("Nhận danh sách {} chữ ký số từ frontend React", request.getCertificates().size());
            
            List<ChuKySoResponse> result = clientCertificateService.saveClientCertificates(request);
            
            log.info("Lưu thành công {} chữ ký số từ frontend React", result.size());
            
            return ResponseHelper.ok(result);
            
        } catch (Exception ex) {
            log.error("Lỗi khi lưu danh sách chữ ký số từ frontend React: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Lưu chữ ký số từ frontend vào database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lưu thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/save-from-frontend")
    public ResponseEntity<?> luuChuKySoTuFrontend(@RequestBody SaveCertificateFromFrontendRequest request) {
        try {
            log.info("Lưu chữ ký số từ frontend với serial number: {}", request.getSerialNumber());
            
            ChuKySoResponse result = frontendCertificateSaveService.saveCertificateFromFrontend(request);
            
            log.info("Lưu thành công chữ ký số từ frontend với serial number: {}", request.getSerialNumber());
            
            return ResponseHelper.ok(result);
            
        } catch (Exception ex) {
            log.error("Lỗi khi lưu chữ ký số từ frontend: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Tạo và ký XML tờ khai với chữ ký số")
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
    @PostMapping("/ky-so")
    public ResponseEntity<?> kySo(@RequestBody XmlGenerationRequest request) {
        try {
            log.info("Nhận yêu cầu tạo XML cho tờ khai ID: {}, lần ký: {}, serial number: {}", 
                    request.getToKhaiId(), request.getLanKy(), request.getSerialNumber());
            
            // Tạo XML từ thông tin tờ khai (không ký)
            String xmlContent = xmlGenerationService.generateAndSaveXml(
                    request.getToKhaiId(), 
                    request.getLanKy(), 
                    request.getSerialNumber()
            );
            
            log.info("Tạo XML thành công cho tờ khai ID: {}, lần ký: {}, serial number: {}", 
                    request.getToKhaiId(), request.getLanKy(), request.getSerialNumber());
            
            // Ký XML với PFX service
            String signedXml = pfxCertificateService.signXmlWithPfxCertificate(xmlContent, null, null);
            
            log.info("Ký XML thành công với PFX cho tờ khai ID: {}", request.getToKhaiId());
            
            return ResponseHelper.ok(signedXml);
            
        } catch (Exception ex) {
            log.error("Lỗi khi tạo và ký XML cho tờ khai ID {} lần ký {} serial number {}: ", 
                    request.getToKhaiId(), request.getLanKy(), request.getSerialNumber(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Ký XML với chữ ký số từ file PFX")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ký thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/ky-so-pfx")
    public ResponseEntity<?> kySoVoiPfx(@RequestBody PfxSignRequest request) {
        try {
            log.info("Nhận yêu cầu ký XML với file PFX: {}", request.getPfxFilePath());
            
            String signedXml = pfxCertificateService.signXmlWithPfxCertificate(
                    request.getXmlContent(), 
                    request.getPfxFilePath(), 
                    request.getPassword()
            );
            
            log.info("Ký XML thành công với file PFX: {}", request.getPfxFilePath());
            
            return ResponseHelper.ok(signedXml);
            
        } catch (Exception ex) {
            log.error("Lỗi khi ký XML với file PFX {}: ", request.getPfxFilePath(), ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Import chữ ký số từ PEM data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Import thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/import")
    public ResponseEntity<?> importCertificate(@RequestBody ImportCertificateRequest request) {
        try {
            log.info("Nhận yêu cầu import certificate cho doanh nghiệp: {}", request.getTenDoanhNghiep());
            
            ImportCertificateResponse response = certificateImportService.importCertificate(request);
            
            log.info("Import certificate thành công với ID: {}", response.getId());
            
            return ResponseHelper.ok(response);
            
        } catch (Exception ex) {
            log.error("Lỗi khi import certificate: ", ex);
            return ResponseHelper.error(ex);
        }
    }

    @Operation(summary = "Import chữ ký số từ file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Import thành công", content = {
                    @Content(schema = @Schema(implementation = ApiDataResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = {
                    @Content(schema = @Schema(implementation = OrderBy.ApiErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/import-file")
    public ResponseEntity<?> importCertificateFromFile(@RequestBody ImportCertificateFileRequest request) {
        try {
            log.info("Nhận yêu cầu import certificate từ file: {}", request.getCertificateFilePath());
            
            ImportCertificateResponse response = certificateFileImportService.importCertificateFromFile(request);
            
            log.info("Import certificate từ file thành công với ID: {}", response.getId());
            
            return ResponseHelper.ok(response);
            
        } catch (Exception ex) {
            log.error("Lỗi khi import certificate từ file: ", ex);
            return ResponseHelper.error(ex);
        }
    }

}
