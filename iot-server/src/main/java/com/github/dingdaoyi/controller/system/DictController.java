package com.github.dingdaoyi.controller.system;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.enu.ServiceTypeEnum;
import com.github.dingdaoyi.entity.enu.StatusEnum;
import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.model.enu.SmsSupplier;
import com.github.dingdaoyi.model.enu.SmsTemplateType;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.ParamType;
import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import com.github.dingdaoyi.utils.EnumUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 字典枚举管理
 * @author dingyunwei
 */
@RestController
@RequestMapping("/dict")
@Tag(name = "字典管理")
public class DictController {
    
    @GetMapping("/sms-supplier")
    @Operation(summary = "短信供应商字典")
    public BaseResult<List<Map<String, Object>>> getSmsSupplier() {
        return BaseResult.success(EnumUtils.enumToMapList(SmsSupplier.class));
    }
    
    @GetMapping("/sms-template-type")
    @Operation(summary = "短信模板类型字典")
    public BaseResult<List<Map<String, Object>>> getSmsTemplateType() {
        return BaseResult.success(EnumUtils.enumToMapList(SmsTemplateType.class));
    }
    
    @GetMapping("/notify-type")
    @Operation(summary = "通知类型字典")
    public BaseResult<List<Map<String, Object>>> getNotifyType() {
        return BaseResult.success(EnumUtils.enumToMapList(NotifyType.class));
    }
    
    @GetMapping("/status")
    @Operation(summary = "状态字典")
    public BaseResult<List<Map<String, Object>>> getStatus() {
        return BaseResult.success(EnumUtils.enumToMapList(StatusEnum.class));
    }
    
    @GetMapping("/service-type")
    @Operation(summary = "服务类型字典")
    public BaseResult<List<Map<String, Object>>> getServiceType() {
        return BaseResult.success(EnumUtils.enumToMapList(ServiceTypeEnum.class));
    }
    
    @GetMapping("/data-type")
    @Operation(summary = "数据类型字典")
    public BaseResult<List<Map<String, Object>>> getDataType() {
        return BaseResult.success(EnumUtils.enumToMapList(DataTypeEnum.class));
    }
    
    @GetMapping("/param-type")
    @Operation(summary = "参数类型字典")
    public BaseResult<List<Map<String, Object>>> getParamType() {
        return BaseResult.success(EnumUtils.enumToMapList(ParamType.class));
    }
    
    @GetMapping("/event-type")
    @Operation(summary = "事件类型字典")
    public BaseResult<List<Map<String, Object>>> getEventType() {
        return BaseResult.success(EnumUtils.enumToMapList(EventTypeEnum.class));
    }
}