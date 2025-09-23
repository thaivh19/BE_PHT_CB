package com.pht.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response thông báo thay đổi trạng thái tờ khai")
public class NotificationResponse {

    @Schema(description = "Số thông báo được tạo", example = "202509263565")
    private String soThongBao;

    @Schema(description = "Message ID", example = "3DA72B5A-0B82-44AF-8DDF-43538D2DC0EE")
    private String msgId;

    @Schema(description = "Trạng thái mới của tờ khai", example = "02")
    private String trangThaiMoi;

    @Schema(description = "ID tờ khai", example = "123")
    private Long toKhaiId;
}


