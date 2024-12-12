package com.github.dingdaoyi.proto.model.tsl;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author dingyunwei
 */
@Data
public class TslEvent {
    /**
     * id
     */
    @Schema(description = "id")
    private Integer id;


    /**
     * 事件类型,INFO,WARN,FAULT
     */
    @Schema(description = "事件类型,INFO,WARN,FAULT")
    private EventTypeEnum eventType;

    /**
     * 标识符
     */
    @Schema(description = "标识符")
    private String identifier;


    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 是否必选
     */
    @TableField(value = "required")
    @Schema(description = "是否必选")
    private Boolean required;

    @Schema(description = "出参")
    public List<TslProperty> outputParams;


    public TslEvent(List<TslProperty> outputParams) {
        this.outputParams = outputParams;
    }
}
