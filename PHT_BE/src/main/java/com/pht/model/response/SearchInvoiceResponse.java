package com.pht.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response tìm kiếm hóa đơn (nguyên từ eInvoice API)")
public class SearchInvoiceResponse {

    @Schema(description = "Mã lỗi", example = "0")
    private String errorCode;

    @Schema(description = "Thông báo lỗi", example = "Success")
    private String errorMessage;

    @Schema(description = "Dữ liệu trả về")
    private Object data;
}

