package com.github.dingdaoyi.service;

/**
 * @author dingyunwei
 * 判断是否使用icon
 */
public interface ResourceUseService<T> {

    /**
     * 是否使用
     *
     * @param data 参数
     * @return
     */
    boolean isUsed(T data);

    /**
     * 使用目标
     *
     * @return
     */
    String userTarget();
}
