package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "Request hủy hóa đơn")
public class CancelInvoiceRequest {

    @Valid
    @NotNull(message = "Thông tin user không được để trống")
    @Schema(description = "Thông tin user", required = true)
    private UserInfo user;

    @Valid
    @NotNull(message = "Thông tin wrongnotice không được để trống")
    @Schema(description = "Thông tin hủy hóa đơn", required = true)
    private WrongNoticeInfo wrongnotice;

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
    @Schema(description = "Thông tin hủy hóa đơn")
    public static class WrongNoticeInfo {
        @NotBlank(message = "STAX không được để trống")
        @Schema(description = "STAX", example = "stax999", required = true)
        private String stax;

        @NotBlank(message = "noti_taxtype không được để trống")
        @Schema(description = "Loại thuế", example = "1", required = true)
        private String noti_taxtype;

        @NotBlank(message = "noti_taxnum không được để trống")
        @Schema(description = "Số thông báo", example = "Số 01", required = true)
        private String noti_taxnum;

        @NotBlank(message = "noti_taxdt không được để trống")
        @Schema(description = "Ngày thông báo", example = "2024-01-01", required = true)
        private String noti_taxdt;

        @Schema(description = "Budget relation ID", example = "")
        private String budget_relationid;

        @NotBlank(message = "place không được để trống")
        @Schema(description = "Nơi hủy", example = "Hà Nội", required = true)
        private String place;

        @Valid
        @NotEmpty(message = "items không được để trống")
        @Schema(description = "Danh sách hóa đơn cần hủy", required = true)
        private List<ItemInfo> items;

        @Data
        @Schema(description = "Thông tin hóa đơn cần hủy")
        public static class ItemInfo {
            @NotBlank(message = "form không được để trống")
            @Schema(description = "Mẫu hóa đơn", example = "01GTKT", required = true)
            private String form;

            @NotBlank(message = "serial không được để trống")
            @Schema(description = "Ký hiệu", example = "AA/24E", required = true)
            private String serial;

            @NotBlank(message = "seq không được để trống")
            @Schema(description = "Số hóa đơn", example = "0000001", required = true)
            private String seq;

            @NotBlank(message = "idt không được để trống")
            @Schema(description = "Ngày hóa đơn", example = "2024-01-01", required = true)
            private String idt;

            @NotNull(message = "type_ref không được để trống")
            @Schema(description = "Loại tham chiếu", example = "1", required = true)
            private Integer type_ref;

            @NotBlank(message = "noti_type không được để trống")
            @Schema(description = "Loại thông báo", example = "1", required = true)
            private String noti_type;

            @Schema(description = "Lý do hủy", example = "")
            private String rea;
        }
    }
}
