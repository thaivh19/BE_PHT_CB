package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "Request thông báo thay đổi trạng thái tờ khai")
public class NotificationRequest {

    @NotNull(message = "ID tờ khai không được để trống")
    @Schema(description = "ID của tờ khai thông tin", example = "123", required = true)
    private Long toKhaiId;
}


