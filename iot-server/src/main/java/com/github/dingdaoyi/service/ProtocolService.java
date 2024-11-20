package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.Protocol;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author dingyunwei
 */
public interface ProtocolService extends IService<Protocol>{

    /**
     * 根据协议key获取
     * @param protoKey
     * @return
     */
    Optional<Protocol> getByProtoKey(String protoKey);

}
