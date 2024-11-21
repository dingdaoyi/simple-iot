package com.github.dingdaoyi.proto.model.tsl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author dingyunwei
 */
@Data
public class TslService {

    public TslService(List<TslProperty> inputParams, List<TslProperty> outputParams) {
        this.inputParams = inputParams;
        this.outputParams = outputParams;
    }

    /**
     * id
     */
    @Schema(description = "id")
    private Integer id;

    /**
     * 服务是否为异步
     */
    @Schema(description = "服务是否为异步")
    private Boolean async;


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
    @Schema(description = "是否必选")
    private Boolean required;


    @Schema(description = "入参")
    public List<TslProperty> inputParams;


    @Schema(description = "出参")
    public List<TslProperty> outputParams;
}
