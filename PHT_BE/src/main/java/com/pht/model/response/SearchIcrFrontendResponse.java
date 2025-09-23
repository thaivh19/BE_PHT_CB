package com.pht.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response tìm kiếm ICR cho frontend")
public class SearchIcrFrontendResponse {

    @Schema(description = "Mã lỗi", example = "0000")
    private String errorCode;

    @Schema(description = "Thông báo lỗi", example = "Success")
    private String errorMessage;

    @Schema(description = "Dữ liệu base64 đã được xử lý (cắt sau 'base64,')")
    private String base64Data;
}
