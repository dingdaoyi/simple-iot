package com.github.dingdaoyi.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局消息ID生成器
 * @author dingyunwei
 */
public class MessageIdGenerator {

    private static final int MAX_VALUE = Integer.MAX_VALUE;
    private static final AtomicInteger CURRENT_ID = new AtomicInteger(0);

    /**
     * 获取下一个消息ID
     * @return 下一个消息ID
     */
    public static int nextId() {
        return CURRENT_ID.getAndUpdate(value -> (value == MAX_VALUE) ? 0 : value + 1);
    }
}