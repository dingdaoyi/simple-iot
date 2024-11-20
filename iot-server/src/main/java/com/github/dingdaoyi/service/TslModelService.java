package com.github.dingdaoyi.service;

import com.github.dingdaoyi.model.DTO.TslModelDTO;

import java.util.Optional;

/**
 * 物模型服务
 * @author dingyunwei
 */
public interface TslModelService {

    Optional<TslModelDTO> findByProductKey(String productKey);
}
