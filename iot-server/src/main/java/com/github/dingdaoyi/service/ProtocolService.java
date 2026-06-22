package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.Protocol;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.EncoderResult;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.proto.model.ProtocolException;

import java.util.List;
import java.util.Map;
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

    /**
     * 测试脚本协议解码，不保存协议草稿，不影响当前已加载协议。
     *
     * @param protocol 协议草稿
     * @param deviceKey 设备Key
     * @param productKey 产品Key
     * @param messageType 消息类型
     * @param data 原始报文
     * @return 解码结果
     */
    DecodeResult testDecode(Protocol protocol, String deviceKey, String productKey,
                            ProtoMessageType messageType, String data) throws ProtocolException;

    /**
     * 测试脚本协议编码，不保存协议草稿，不影响当前已加载协议。
     *
     * @param protocol 协议草稿
     * @param deviceKey 设备Key
     * @param productKey 产品Key
     * @param identifier 功能/属性标识符
     * @param params 下行参数
     * @return 编码结果
     */
    EncoderResult testEncode(Protocol protocol, String deviceKey, String productKey,
                             String identifier, Map<String, Object> params) throws ProtocolException;

}
