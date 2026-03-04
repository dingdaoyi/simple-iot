package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.Protocol;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
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

    /**
     * 条件查询协议列表
     * @param name 协议名称（模糊查询）
     * @param protoType 协议类型
     * @param status 状态
     * @param protoKey 协议Key（模糊查询）
     * @param scriptLang 脚本语言
     * @return 协议列表
     */
    List<Protocol> list(String name, Integer protoType, Integer status, String protoKey, String scriptLang);

    /**
     * 设置协议状态
     * @param id 协议ID
     * @param enable true=启用, false=禁用
     * @return 是否成功
     */
    boolean setProtocolStatus(Integer id, boolean enable);

}
