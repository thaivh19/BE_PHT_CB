package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request lọc tờ khai theo ngày và trạng thái")
public class ToKhaiFilterRequest {
    
    @Schema(description = "Ngày bắt đầu", example = "2024-01-01")
    private LocalDate tuNgay;
    
    @Schema(description = "Ngày kết thúc", example = "2024-12-31")
    private LocalDate denNgay;
    
    @Schema(description = "Trạng thái tờ khai", example = "02")
    private String trangThai;
}
