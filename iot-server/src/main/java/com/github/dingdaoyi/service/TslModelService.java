package com.github.dingdaoyi.service;

import com.github.dingdaoyi.proto.model.tsl.TslModel;

import java.util.Optional;

/**
 * 物模型服务
 * @author dingyunwei
 */
public interface TslModelService {

    Optional<TslModel> findByProductKey(String productKey);
}
