package com.github.dingdaoyi.model;

/**
 * 请求参数转换为数据库实体
 * @author dingyunwei
 */
public interface ToEntity<T> {
    T toEntity();
}
