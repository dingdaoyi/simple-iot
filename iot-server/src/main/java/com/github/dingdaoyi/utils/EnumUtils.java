package com.github.dingdaoyi.utils;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 枚举工具类
 * @author dingyunwei
 */
public class EnumUtils {
    
    /**
     * 将枚举转换为Map列表，包含所有字段
     */
    public static <T extends Enum<T>> List<Map<String, Object>> enumToMapList(Class<T> enumClass) {
        List<Map<String, Object>> result = new ArrayList<>();
        T[] enumConstants = enumClass.getEnumConstants();
        
        for (T enumConstant : enumConstants) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", enumConstant.name());
            map.put("ordinal", enumConstant.ordinal());
            
            Field[] fields = enumClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isEnumConstant()) {
                    continue;
                }
                
                try {
                    field.setAccessible(true);
                    Object value = field.get(enumConstant);
                    
                    if (field.isAnnotationPresent(EnumValue.class)) {
                        map.put("code", value);
                    }
                    if (field.isAnnotationPresent(JsonValue.class)) {
                        map.put("value", value);
                    }
                    
                    map.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    // 忽略无法访问的字段
                }
            }
            
            result.add(map);
        }
        
        return result;
    }
}