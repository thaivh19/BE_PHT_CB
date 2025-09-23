package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "Request xóa hóa đơn chưa phát hành")
public class DeleteInvoiceRequest {

    @Valid
    @NotNull(message = "Thông tin user không được để trống")
    @Schema(description = "Thông tin user", required = true)
    private UserInfo user;

    @Valid
    @NotNull(message = "Thông tin ref_inv không được để trống")
    @Schema(description = "Thông tin hóa đơn", required = true)
    private RefInvInfo refInv;

    @NotBlank(message = "SID không được để trống")
    @Schema(description = "Session ID", example = "session123", required = true)
    private String sid;

    @NotBlank(message = "STAX không được để trống")
    @Schema(description = "STAX", example = "stax999", required = true)
    private String stax;

    @Data
    @Schema(description = "Thông tin user")
    public static class UserInfo {
        @NotBlank(message = "Username không được để trống")
        @Schema(description = "Tên đăng nhập", example = "admin", required = true)
        private String username;

        @NotBlank(message = "Password không được để trống")
        @Schema(description = "Mật khẩu", example = "password123", required = true)
        private String password;
    }

    @Data
    @Schema(description = "Thông tin hóa đơn")
    public static class RefInvInfo {
        @NotBlank(message = "INC không được để trống")
        @Schema(description = "Số hóa đơn", example = "INV001", required = true)
        private String inc;
    }
}
