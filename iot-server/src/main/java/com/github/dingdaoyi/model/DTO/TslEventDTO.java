package com.github.dingdaoyi.model.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.entity.enu.EventTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.dreamlu.mica.core.utils.$;

import java.util.List;

/**
 * @author dingyunwei
 */
@Data
public class TslEventDTO {
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
    private String mark;

    /**
     * 是否必选
     */
    @TableField(value = "required")
    @Schema(description = "是否必选")
    private Boolean required;

    @Schema(description = "出参")
    public List<TslPropertyDTO> outputParams;


    public TslEventDTO(ModelService modelService, List<TslPropertyDTO> outputParams) {
        $.copy(modelService,this);
        this.outputParams = outputParams;
    }
}
