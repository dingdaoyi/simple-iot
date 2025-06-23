package com.github.dingdaoyi.iot.impl;


import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.github.dingdaoyi.iot.model.CommandResponse;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.proto.model.DecodeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author dwx
 */
@Component
@Slf4j
public class ResponseRegister {

    private static final long TIME_OUT = 1200;

    public final ConcurrentMap<String, CompletableFuture<CommandResponse>> localRequestMap = new ConcurrentHashMap<>();
    
    /**
     * 注册响应
     *
     * @param messageId
     * @param deviceKey
     * @param future
     */
    public void register(int messageId, String deviceKey, CompletableFuture<CommandResponse> future) {
        String redisKey = getKey(messageId, deviceKey);
        CompletableFuture.delayedExecutor(TIME_OUT, TimeUnit.SECONDS)
            .execute(() -> {
                CompletableFuture<CommandResponse> completableFuture = localRequestMap.get(redisKey);
                // 如果还没有完成,踢掉
                if (completableFuture != null &&(future==completableFuture)&& (!completableFuture.isDone())) {
                    log.error("调用超时|{}|{}...",messageId,deviceKey);
                    completableFuture.completeExceptionally(new BusinessException(ResultCode.BAD_REQUEST,"设备未回复,调用超时"));
                    localRequestMap.remove(redisKey);
                }
            });
        localRequestMap.put(redisKey, future);
    }

    private String getKey(int messageId, String deviceKey) {
        return deviceKey + StringPool.COLON + messageId;
    }


    /**
     * 是否为响应码
     *
     * @param messageId
     * @param deviceKey
     * @return
     */
    public boolean isResponse(int messageId, String deviceKey) {
        return  localRequestMap.containsKey(getKey(messageId, deviceKey));
    }

    /**
     * 完成事件
     *
     * @param result 解析数据的结果
     */
    public void complete(DecodeResult result,String deviceKey) {
        String redisKey = getKey(result.getMessageId(), deviceKey);
        if (localRequestMap.containsKey(redisKey)) {
            CompletableFuture<CommandResponse> future = localRequestMap.get(redisKey);
            log.info("终端回复消息:{}|{}", deviceKey, result.getRowData());
            CommandResponse commandResponse = new CommandResponse();
            commandResponse.setResultData(result.getServiceResData().toMap());
            future.complete(commandResponse);
        }
        localRequestMap.remove(redisKey);
    }
}
