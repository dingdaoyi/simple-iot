package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.entity.enu.ProtoType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Schema
@Data
@TableName(value = "tb_protocol")
public class Protocol {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    /**
     * 协议名称
     */
    @TableField(value = "name")
    @Schema(description = "协议名称")
    private String name;

    /**
     * 协议类型
     */
    @TableField(value = "proto_type")
    @Schema(description = "协议类型")
    private ProtoType protoType;

    /**
     * 协议包地址
     */
    @TableField(value = "url")
    @Schema(description = "协议包地址")
    private String url;

    /**
     * 协议描述
     */
    @TableField(value = "mark")
    @Schema(description = "协议描述")
    private String mark;

    /**
     * 协议key
     */
    @TableField(value = "proto_key")
    @Schema(description = "协议key")
    private String protoKey;

    @TableField(value = "handler_class")
    @Schema(description = "协议入口")
    private String handlerClass;

    /**
     * 脚本内容 (仅用于脚本类型协议)
     */
    @TableField(value = "script_content")
    @Schema(description = "脚本内容")
    private String scriptContent;

    /**
     * 脚本语言类型 (javascript, groovy, python, lua)
     */
    @TableField(value = "script_lang")
    @Schema(description = "脚本语言类型")
    private String scriptLang;

    /**
     * 状态 (1=启用, 2=禁用)
     */
    @TableField(value = "status")
    @Schema(description = "状态 (1=启用, 2=禁用)")
    private Integer status;

    // ==================== 业务方法 ====================

    /**
     * 是否为启用状态
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }

    /**
     * 是否为脚本类型协议
     */
    public boolean isScriptType() {
        return protoType == ProtoType.JAVASCRIPT;
    }

    /**
     * 是否为系统默认协议
     */
    public boolean isSystemDefault() {
        return protoType == ProtoType.SYSTEM_DEFAULT;
    }

    /**
     * 是否为 Java 类型协议
     */
    public boolean isJavaType() {
        return protoType == ProtoType.JAVA;
    }

    /**
     * 是否有脚本内容
     */
    public boolean hasScriptContent() {
        return scriptContent != null && !scriptContent.isEmpty();
    }
}