package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "Request tìm kiếm hóa đơn")
public class SearchInvoiceRequest {

    @NotBlank(message = "stax không được để trống")
    @Schema(description = "STAX - Mã số thuế", example = "0318680861", required = true)
    private String stax;

    @Schema(description = "Loại response", example = "pdf", defaultValue = "json")
    private String type = "json";

    @NotBlank(message = "sid không được để trống")
    @Schema(description = "Session ID", example = "SonTEST072025027", required = true)
    private String sid;

    @Valid
    @NotNull(message = "Thông tin user không được để trống")
    @Schema(description = "Thông tin user", required = true)
    private UserInfo user;

    @Schema(description = "ID của tờ khai thông tin để lưu base64 response", example = "123")
    private Long toKhaiId;

    @Data
    @Schema(description = "Thông tin user")
    public static class UserInfo {
        @NotBlank(message = "Username không được để trống")
        @Schema(description = "Tên đăng nhập", example = "0318680861.MPOS", required = true)
        private String username;

        @NotBlank(message = "Password không được để trống")
        @Schema(description = "Mật khẩu", example = "Admin@123", required = true)
        private String password;
    }
}
