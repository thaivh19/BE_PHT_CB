package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "Request cập nhật trạng thái phát hành")
public class UpdateTrangThaiPhatHanhRequest {

    @NotNull(message = "ID không được để trống")
    @Schema(description = "ID của tờ khai thông tin cần cập nhật trạng thái phát hành", example = "123", required = true)
    private Long id;

    @Schema(description = "Trạng thái phát hành mới", example = "02")
    private String trangThaiPhatHanh;
}