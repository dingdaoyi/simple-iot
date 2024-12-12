package com.github.dingdaoyi.iot;

import com.github.dingdaoyi.iot.model.CommandRequest;
import com.github.dingdaoyi.iot.model.CommandResponse;
import com.github.dingdaoyi.proto.model.tsl.TslModel;

import java.util.concurrent.CompletableFuture;

/**
 * 指令下发
 */
public interface IotCommandProcessor {

    /**
     * 指令下发
     * @param request
     * @return
     */
    CompletableFuture<CommandResponse> messageDown(CommandRequest request, TslModel tslModel);
}
