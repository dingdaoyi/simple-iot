package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.dingdaoyi.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_dashboard", autoResultMap = true)
@Schema(description = "自定义仪表盘")
public class Dashboard extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "仪表盘名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "布局配置: widget 数组")
    @TableField(value = "layout", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> layout;

    @Schema(description = "是否默认仪表盘")
    private Boolean isDefault;
}
