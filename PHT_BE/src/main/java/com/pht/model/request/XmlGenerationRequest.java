package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "Request tạo XML từ thông tin tờ khai")
public class XmlGenerationRequest {

    @NotNull(message = "ID tờ khai không được để trống")
    @Schema(description = "ID của tờ khai thông tin", example = "123", required = true)
    private Long toKhaiId;

    @Schema(description = "Lần ký (1: lưu vào KYLAN1_XML, khác: lưu vào KYLAN2_XML)", example = "1", defaultValue = "1")
    private Integer lanKy = 1;

    @Schema(description = "Serial Number của chữ ký số để ký XML (lấy từ danh sách chữ ký số có sẵn)", example = "1234567890ABCDEF")
    private String serialNumber;
}
