package com.github.dingdaoyi.model.query;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class LoginQuery {
    @NotBlank(message = "账号不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
