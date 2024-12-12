package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.DeviceCommand;
import com.github.dingdaoyi.entity.enu.CommandStatus;
import com.github.dingdaoyi.iot.IotCommandProcessor;
import com.github.dingdaoyi.iot.model.CommandRequest;
import com.github.dingdaoyi.iot.model.CommandResponse;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.proto.model.ProtocolException;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.proto.model.tsl.TslService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.exception.ServiceException;
import net.dreamlu.mica.core.utils.CollectionUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author dyw
 */
@Service
@Slf4j
@AllArgsConstructor
public class ServiceHandler {
    private final DeviceService deviceService;
    private final TslModelService tslModelService;
    private final DeviceCommandService commandService;
    private final IotCommandProcessor commandProcessor;
    public Map<String, Object> sendMessage(String deviceKey, String identifier, Map<String, Object> paramMap)
            throws ServiceException {
        if (CollectionUtil.isEmpty(paramMap)) {
            paramMap = new HashMap<>();
        }
        final Optional<DeviceDTO> optional = deviceService.getByDeviceKey(deviceKey);
        if (optional.isEmpty()) {
            throw new ServiceException(SysCodeEnum.BAD_REQUEST, "device not found: " + deviceKey);
        }
        DeviceDTO device = optional.get();
        //TODO 判断通道是否激活
        Optional<TslModel> tslModelOptional = tslModelService.findByProductKey(device.getProductKey());
        if (tslModelOptional.isEmpty()) {
            throw new ServiceException(SysCodeEnum.BAD_REQUEST, "tsl model not found: " + deviceKey);
        }
        TslModel tslModel = tslModelOptional.get();
        Optional<TslService> tslService = tslModel.serviceByIdentifier(identifier);
        if (tslService.isEmpty()) {
            throw new ServiceException(SysCodeEnum.BAD_REQUEST, "服务未定义在物模型中,无法下发指令:" + identifier);
        }
        TslService service = tslService.get();
        validParam(paramMap, service);
//        // 是否为缓存命令
        DeviceCommand deviceCommand = new DeviceCommand();
        deviceCommand.setDeviceKey(device.getDeviceKey());
        deviceCommand.setInputParams(paramMap);
        deviceCommand.setIdentifier(identifier);
        deviceCommand.setProtoKey(device.getProtoKey());
        deviceCommand.setProductKey(device.getProductKey());
        if (service.isAsync()) {
            deviceCommand.setStatus(CommandStatus.SAVED);
            commandService.save(deviceCommand);
            return Collections.singletonMap("commandId", deviceCommand.getId());
        } else {
            // 保存指令下发日志
            deviceCommand.setStatus(CommandStatus.DELIVERED);
            commandService.save(deviceCommand);
        }

        CompletableFuture<CommandResponse> future = commandProcessor.messageDown(new CommandRequest(deviceCommand),tslModel);
        try {
            CommandResponse result = future.get();
            Map<String, Object> resultData = result.getResultData();
            if (CollectionUtil.isEmpty(resultData)) {
                return Collections.emptyMap();
            }
            return new HashMap<>(resultData);
        } catch (InterruptedException e) {
            throw new ServiceException(SysCodeEnum.BAD_REQUEST, e.getMessage());
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ServiceException se) {
                throw se;
            }
            if (e.getCause() instanceof ProtocolException se) {
                throw new ServiceException(SysCodeEnum.BAD_REQUEST, se.getMessage());
            }
            throw new ServiceException(SysCodeEnum.BAD_REQUEST, e.getMessage());
        }
    }

    private void validParam(Map<String, Object> paramMap, TslService service) {
        List<TslProperty> inputParam = service.getInputParams();
        if (CollectionUtil.isNotEmpty(inputParam)) {
            for (TslProperty tslProperty : inputParam) {
                String identifier = tslProperty.getIdentifier();
                if (paramMap.containsKey(identifier)) {
                    Object param = paramMap.get(identifier);
                    if (!tslProperty.validType(param)) {
                        throw new ServiceException(SysCodeEnum.BAD_REQUEST, "参数数据类型错误:" + identifier);
                    }
                }
            }
        }
    }
}
