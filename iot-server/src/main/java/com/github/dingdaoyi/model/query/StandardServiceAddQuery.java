package com.github.dingdaoyi.model.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.entity.ServiceProperty;
import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import com.github.dingdaoyi.entity.enu.ServiceTypeEnum;
import com.github.dingdaoyi.entity.enu.StatusEnum;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.dreamlu.mica.core.utils.$;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingyunwei
 */
@Data
public class StandardServiceAddQuery implements ToEntity<ModelService> {


    /**
     * 服务是否为异步
     */
    @Schema(description = "服务是否为异步")
    private Boolean async;

    /**
     * 事件类型,INFO,WARN,FAULT
     */
    @Schema(description = "事件类型,INFO,WARN,FAULT")
    private EventTypeEnum eventType;


    @Schema(description = "服务类型,1,服务,2事件")
    @NotNull(message = "服务类型不能为空")
    private ServiceTypeEnum serviceType;

    /**
     * 标识符
     */
    @Schema(description = "标识符")
    @NotBlank(message = "标识符不能为空")
    private String identifier;

    /**
     * 服务输入参数
     */
    @Schema(description = "服务输入参数")
    private List<Integer> inputParamIds;

    /**
     * 名称
     */
    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String mark;

    /**
     * 出参
     */
    @Schema(description = "出参")
    private List<Integer> outputParamIds;

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
     * 是否必选
     */
    @TableField(value = "required")
    @Schema(description = "是否必选")
    private Boolean required;


    @Override
    public ModelService toEntity() {
        ModelService modelService = new ModelService();
        modelService.setIdentifier(this.identifier);
        modelService.setName(this.name);
        modelService.setMark(this.mark);
        modelService.setProductTypeId(this.productTypeId);
        modelService.setServiceType(this.serviceType);
        modelService.setEventType(this.eventType);
        modelService.setAsync(this.async);
        modelService.setRequired(this.required);
        modelService.setIconId(this.iconId);
        modelService.setCustom(false);
        return modelService;
    }

    public List<ServiceProperty> getServiceProperties(Integer serviceId) {
        List<ServiceProperty> serviceProperties = new ArrayList<>();
        if ($.isNotEmpty(outputParamIds)) {
            for (Integer propertyId : outputParamIds) {
                serviceProperties.add(new ServiceProperty(serviceId, propertyId, ServiceProperty.OUTPUT_TYPE));
            }
        }
        if ($.isNotEmpty(inputParamIds)) {
            for (Integer propertyId : inputParamIds) {
                serviceProperties.add(new ServiceProperty(serviceId, propertyId, ServiceProperty.INPUT_TYPE));
            }
        }
        return serviceProperties;
    }
}
