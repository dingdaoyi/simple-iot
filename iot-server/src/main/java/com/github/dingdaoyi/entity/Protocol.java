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
}