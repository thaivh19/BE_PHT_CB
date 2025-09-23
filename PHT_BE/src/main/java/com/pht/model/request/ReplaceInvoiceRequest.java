package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "Request thay thế hóa đơn")
public class ReplaceInvoiceRequest {

    @NotBlank(message = "lang không được để trống")
    @Schema(description = "Ngôn ngữ", example = "vi", required = true)
    private String lang;

    @Valid
    @NotNull(message = "Thông tin user không được để trống")
    @Schema(description = "Thông tin user", required = true)
    private UserInfo user;

    @Valid
    @NotNull(message = "Thông tin inv không được để trống")
    @Schema(description = "Thông tin hóa đơn", required = true)
    private InvInfo inv;

    @Data
    @Schema(description = "Thông tin user")
    public static class UserInfo {
        @NotBlank(message = "Username không được để trống")
        @Schema(description = "Tên đăng nhập", example = "222222222211.admin", required = true)
        private String username;

        @NotBlank(message = "Password không được để trống")
        @Schema(description = "Mật khẩu", example = "password123", required = true)
        private String password;
    }

    @Data
    @Schema(description = "Thông tin hóa đơn")
    public static class InvInfo {
        @Valid
        @NotNull(message = "Thông tin adj không được để trống")
        @Schema(description = "Thông tin điều chỉnh", required = true)
        private AdjInfo adj;

        @NotBlank(message = "sid không được để trống")
        @Schema(description = "Session ID", example = "session123", required = true)
        private String sid;

        @Schema(description = "Ngày hóa đơn", example = "2024-01-01")
        private String idt;

        @NotBlank(message = "type không được để trống")
        @Schema(description = "Loại hóa đơn", example = "01/MTT", required = true)
        private String type;

        @NotBlank(message = "form không được để trống")
        @Schema(description = "Mẫu hóa đơn", example = "1", required = true)
        private String form;

        @Schema(description = "Ký hiệu", example = "AA/24E")
        private String serial;

        @Schema(description = "Số hóa đơn", example = "0000001")
        private String seq;

        @NotNull(message = "aun không được để trống")
        @Schema(description = "AUN", example = "2", required = true)
        private Integer aun;

        @Schema(description = "Is sign XML", example = "")
        private String is_sign_xml;

        @Schema(description = "Mã cơ quan thu", example = "")
        private String ma_cqthu;

        @NotBlank(message = "bname không được để trống")
        @Schema(description = "Tên người bán", example = "Công ty TNHH…", required = true)
        private String bname;

        @NotBlank(message = "baddr không được để trống")
        @Schema(description = "Địa chỉ người bán", example = "API_địa chỉ", required = true)
        private String baddr;

        @NotBlank(message = "buyer không được để trống")
        @Schema(description = "Người mua", example = "API_ng mua", required = true)
        private String buyer;

        @Schema(description = "Phương thức thanh toán", example = "Điểm")
        private String paym;

        @NotBlank(message = "btax không được để trống")
        @Schema(description = "Mã số thuế người bán", example = "2222222222", required = true)
        private String btax;

        @Schema(description = "Số điện thoại người bán", example = "+840438250000")
        private String btel;

        @Schema(description = "Email người bán", example = "abc@fpt.com.vn")
        private String bmail;

        @Schema(description = "Ghi chú", example = "")
        private String note;

        @NotNull(message = "sumv không được để trống")
        @Schema(description = "Tổng tiền trước thuế", example = "1100", required = true)
        private Double sumv;

        @NotNull(message = "sum không được để trống")
        @Schema(description = "Tổng tiền", example = "1100", required = true)
        private Double sum;

        @NotNull(message = "vatv không được để trống")
        @Schema(description = "Thuế VAT", example = "90", required = true)
        private Double vatv;

        @NotNull(message = "vat không được để trống")
        @Schema(description = "VAT", example = "90", required = true)
        private Double vat;

        @Schema(description = "Bằng chữ", example = "")
        private String word;

        @NotNull(message = "totalv không được để trống")
        @Schema(description = "Tổng tiền có thuế", example = "1190", required = true)
        private Double totalv;

        @NotNull(message = "total không được để trống")
        @Schema(description = "Tổng cộng", example = "1190", required = true)
        private Double total;

        @Schema(description = "Số tiền giao dịch", example = "0")
        private Double tradeamount;

        @Schema(description = "Giảm giá", example = "")
        private String discount;

        @NotNull(message = "type_ref không được để trống")
        @Schema(description = "Loại tham chiếu", example = "1", required = true)
        private Integer type_ref;

        @NotNull(message = "notsendmail không được để trống")
        @Schema(description = "Không gửi email", example = "1", required = true)
        private Integer notsendmail;

        @NotNull(message = "sendfile không được để trống")
        @Schema(description = "Gửi file", example = "1", required = true)
        private Integer sendfile;

        @Valid
        @NotEmpty(message = "items không được để trống")
        @Schema(description = "Danh sách hàng hóa", required = true)
        private List<ItemInfo> items;

        @NotBlank(message = "stax không được để trống")
        @Schema(description = "STAX", example = "222222222211", required = true)
        private String stax;

        @Data
        @Schema(description = "Thông tin điều chỉnh")
        public static class AdjInfo {
            @NotBlank(message = "rdt không được để trống")
            @Schema(description = "Ngày điều chỉnh", example = "2024-01-01", required = true)
            private String rdt;

            @Schema(description = "Lý do điều chỉnh", example = "")
            private String rea;

            @NotBlank(message = "ref không được để trống")
            @Schema(description = "Tham chiếu", example = "123", required = true)
            private String ref;

            @NotBlank(message = "seq không được để trống")
            @Schema(description = "Số hóa đơn cũ", example = "1-AA/24E-0000001", required = true)
            private String seq;
        }

        @Data
        @Schema(description = "Thông tin hàng hóa")
        public static class ItemInfo {
            @NotNull(message = "line không được để trống")
            @Schema(description = "Dòng", example = "1", required = true)
            private Integer line;

            @Schema(description = "Loại", example = "CK")
            private String type;

            @NotBlank(message = "vrt không được để trống")
            @Schema(description = "Thuế suất", example = "8", required = true)
            private String vrt;

            @NotBlank(message = "code không được để trống")
            @Schema(description = "Mã hàng hóa", example = "HH gốc", required = true)
            private String code;

            @NotBlank(message = "name không được để trống")
            @Schema(description = "Tên hàng hóa", example = "Tivi LG", required = true)
            private String name;

            @NotBlank(message = "unit không được để trống")
            @Schema(description = "Đơn vị tính", example = "chiếc", required = true)
            private String unit;

            @NotNull(message = "price không được để trống")
            @Schema(description = "Đơn giá", example = "1000", required = true)
            private Double price;

            @NotNull(message = "quantity không được để trống")
            @Schema(description = "Số lượng", example = "1", required = true)
            private Double quantity;

            @NotNull(message = "amount không được để trống")
            @Schema(description = "Thành tiền", example = "1000", required = true)
            private Double amount;
        }
    }
}
