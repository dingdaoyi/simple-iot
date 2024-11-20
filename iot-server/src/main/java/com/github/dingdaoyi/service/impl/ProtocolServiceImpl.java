package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.ProtocolMapper;
import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.service.ProtocolService;

/**
 * @author dingyunwei
 */
@Service
public class ProtocolServiceImpl extends ServiceImpl<ProtocolMapper, Protocol> implements ProtocolService {

    @Override
    public Optional<Protocol> getByProtoKey(String protoKey) {
        return Optional.ofNullable(
                getOne(
                        Wrappers
                                .<Protocol>lambdaQuery()
                                .eq(Protocol::getProtoKey, protoKey)
                )
        );
    }
}
