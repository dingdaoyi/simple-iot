package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import com.github.dingdaoyi.entity.enu.ServiceTypeEnum;
import com.github.dingdaoyi.entity.enu.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 物模型服务表
 *
 * @author dingyunwei
 */
@Schema(description = "物模型服务表")
@Data
@TableName(value = "tb_model_service")
public class ModelService {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    /**
     * 服务是否为异步
     */
    @TableField(value = "async")
    @Schema(description = "服务是否为异步")
    private Boolean async;

    /**
     * 事件类型,INFO,WARN,FAULT
     */
    @TableField(value = "event_type")
    @Schema(description = "事件类型,INFO,WARN,FAULT")
    private EventTypeEnum eventType;

    /**
     * 功能状态,1 启用,0禁用
     */
    @TableField(value = "status")
    @Schema(description = "功能状态,1 启用,0禁用")
    private StatusEnum status;

    @TableField(value = "service_type")
    @Schema(description = "")
    private ServiceTypeEnum serviceType;

    /**
     * 标识符
     */
    @TableField(value = "identifier")
    @Schema(description = "标识符")
    private String identifier;


    /**
     * 名称
     */
    @TableField(value = "name")
    @Schema(description = "名称")
    private String name;

    /**
     * 备注
     */
    @TableField(value = "mark")
    @Schema(description = "备注")
    private String mark;

    /**
     * 产品类型id
     */
    @TableField(value = "product_type_id")
    @Schema(description = "产品类型id")
    private Integer productTypeId;

    /**
     * 图标id
     */
    @TableField(value = "icon_id")
    @Schema(description = "图标id")
    private Integer iconId;

    /**
     * 是否为自定义
     */
    @TableField(value = "custom")
    @Schema(description = "是否为自定义")
    private Boolean custom;

    /**
     * 是否必选
     */
    @TableField(value = "required")
    @Schema(description = "是否必选")
    private Boolean required;

    @TableField(value = "product_id")
    @Schema(description = "产品id")
    private Integer productId;
}