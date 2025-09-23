package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request tạo ICR e-invoice")
public class CreateIcrRequest {
    
    @Schema(description = "Thông tin user")
    private Object user;
    
    @Schema(description = "Thông tin biên lai/receipt theo spec FPT")
    private Object receipt;
    
    @Schema(description = "ID tờ khai thông tin để cập nhật trạng thái phát hành", example = "123")
    private Long toKhaiId;
}

