package com.github.dingdaoyi.iot.impl;

import com.github.dingdaoyi.iot.DataProcessor;
import com.github.dingdaoyi.iot.DeviceChannelManager;
import com.github.dingdaoyi.iot.IoTDataProcessor;
import com.github.dingdaoyi.iot.IotCommandProcessor;
import com.github.dingdaoyi.iot.model.CommandRequest;
import com.github.dingdaoyi.iot.model.CommandResponse;
import com.github.dingdaoyi.iot.proto.ProtocolFactory;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.TslModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class IoTDataProcessorImpl implements IoTDataProcessor, IotCommandProcessor {
    @Resource
    private ExecutorService executorService;
    @Resource
    private List<DataProcessor> dataProcessingServices;
    @Resource
    private TslModelService tslModelService;

    @Resource
    private DeviceChannelManager deviceChannelManager;

    @Resource
    private ResponseRegister responseRegister;

    @Resource
    private DeviceService deviceService;

    @Override
    public void messageUp(DeviceRequest request) {
        Optional<TslModel> tslOptional = tslModelService.findByProductKey(request.getProductKey());
        String deviceKey = request.getDeviceKey();
        if (tslOptional.isEmpty()) {
            log.info("产品对应物模型不存在,忽略:{}|{}", request.getProductKey(), deviceKey);
            return;
        }
        Optional<ProtocolDecoder> optional = ProtocolFactory.getDecoder(request.getProtoKey());
        if (optional.isEmpty()) {
            log.warn("协议解析器不存在,需检查产品信息:{}", request.getProtoKey());
            return;
        }
        ProtocolDecoder decoder = optional.get();
        try {
            DecodeResult result = decoder.decode(request,tslOptional.get());
            // 处理结果
            if (responseRegister.isResponse(result.getMessageId(), deviceKey)) {
                responseRegister.complete(result,deviceKey);
            }
            // 数据后续处理
            for (DataProcessor processingService : dataProcessingServices) {
                executorService.submit(() -> processingService.process(result,deviceKey));
            }
        } catch (ProtocolException e) {
            log.error("协议解析失败,原因:{}", e.getMessage());
            decoder.responseError(request.getConnection(),e);
        }
    }

    @Override
    public void oline(String deviceKey) {
        deviceService.updateOlinStatus(deviceKey,true);
    }

    @Override
    public void offline(String deviceKey) {
        deviceService.updateOlinStatus(deviceKey,false);
    }

    @Override
    public CompletableFuture<CommandResponse> messageDown(CommandRequest request,TslModel tslModel) {
        CompletableFuture<CommandResponse> future = new CompletableFuture<>();
        Optional<ProtocolDecoder> decoderOptional = ProtocolFactory.getDecoder(request.getProtoKey());
        if (decoderOptional.isEmpty()) {
            future.completeExceptionally(new ServiceException(SysCodeEnum.PROTO_NOT_EXIST));
            return future;
        }
        ProtocolDecoder protocolDecoder = decoderOptional.get();
        try {
            EncoderMessage encoderMessage = request.toMessage();
            EncoderResult result = protocolDecoder.encode(encoderMessage,tslModel);
            if (result == null) {
                throw new ServiceException(SysCodeEnum.BAD_REQUEST,"此设备不支持调用此服务，有疑问请联系管理员！");
            }
            //异步事件不需要响应时
            if (result.isNeedReply()) {
                deviceChannelManager.sendMessage(request.getDeviceKey(), result.getMetadata(), result.getMessage());
                responseRegister.register(result.getMessageId(), request.getDeviceKey(), future);
                log.info("DOWN_MESSAGE_SUCCESS,{}|{}|{}", result.getMessageId(), request.getDeviceKey(), new String(result.getMessage()));
            } else  {
                final CommandResponse response = new CommandResponse();
                response.setResultData(Map.of("result", "已发送"));
                future.complete(response);
            }
        } catch (ProtocolException e) {
            future.completeExceptionally(e);
        }
        return future;
    }
}
