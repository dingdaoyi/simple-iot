package com.github.dingdaoyi.proto.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存key value值
 * @author dingyunwei
 * @param <K>
 * @param <V>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyValue<K,V> {
    private K key;
    private V value;
}
