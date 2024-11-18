package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.dreamlu.mica.core.utils.$;

/**
 * @author dingyunwei
 */
@Schema
@Data
@TableName(value = "tb_user")
public class User  {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="")
    private Integer id;

    @TableField(value = "username")
    @Schema(description="")
    private String username;

    @TableField(value = "\"password\"")
    @Schema(description="")
    private String password;

    @TableField(value = "enabled")
    @Schema(description="")
    private Boolean enabled;

    @TableField(value = "account_non_expired")
    @Schema(description="")
    private Boolean accountNonExpired;

    @TableField(value = "account_non_locked")
    @Schema(description="")
    private Boolean accountNonLocked;

    @TableField(value = "credentials_non_expired")
    @Schema(description="")
    private Boolean credentialsNonExpired;

    public boolean isLocked() {
        return $.isFalse(accountNonLocked);
    }

}