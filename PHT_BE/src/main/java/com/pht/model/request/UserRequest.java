package com.pht.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Thông tin user")
public class UserRequest {
    
    @Schema(description = "Tên đăng nhập", example = "0304126484.bl")
    private String username;
    
    @Schema(description = "Mật khẩu", example = "admin@123")
    private String password;
}
