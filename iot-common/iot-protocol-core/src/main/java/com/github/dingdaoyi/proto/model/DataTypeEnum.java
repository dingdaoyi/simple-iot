package com.github.dingdaoyi.proto.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author dingyunwei
 */

@SuppressWarnings("ALL")
@Getter
public enum DataTypeEnum {
    INT(1, "整型", Integer.class) {
        @Override
        public boolean validType(Object value) {
            if (value instanceof Integer) return true;
            try {
                Integer.parseInt(value.toString());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Integer parse(Object value, TslProperty tslProperty) throws IllegalArgumentException {
            return Integer.parseInt(value.toString());
        }
    },
    FLOAT(2, "float浮点型", Float.class) {
        @Override
        public boolean validType(Object value) {
            if (value instanceof Float) return true;
            try {
                Float.parseFloat(value.toString());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Float parse(Object value, TslProperty tslProperty) throws IllegalArgumentException {
            return Float.parseFloat(value.toString());
        }
    },
    DOUBLE(3, "double浮点型", Double.class) {
        @Override
        public boolean validType(Object value) {
            if (value instanceof Double) return true;
            try {
                Double.parseDouble(value.toString());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Double parse(Object value, TslProperty tslProperty) throws IllegalArgumentException {
            return Double.parseDouble(value.toString());
        }
    },
    ENUM(4, "枚举型", String.class) {
        @Override
        public boolean validType(Object value) {
            if (value instanceof Integer) return true;
            try {
                Integer.parseInt(value.toString());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public String parse(Object value, TslProperty tslProperty) throws IllegalArgumentException {
            List<KeyValue<Integer, String>> enums = tslProperty.getEnums();
            if (enums == null || enums.size() == 0) {
                return "";
            }
            return enums.stream()
                    .filter(e -> e.getKey().equals(Integer.valueOf(value.toString())))
                    .findFirst()
                    .map(KeyValue::getValue).orElse("");
        }
    },
    TEXT(5, "字符串", String.class) {
        @Override
        public boolean validType(Object value) {
            return value instanceof String;
        }

        @Override
        public String parse(Object value, TslProperty tslProperty) throws IllegalArgumentException {
            return value.toString();
        }
    },
    BOOL(6, "布尔", Boolean.class) {
        @Override
        public boolean validType(Object value) {
            if (value instanceof Boolean) return true;
            String str = value.toString().toLowerCase();
            return "true".equals(str) || "false".equals(str);
        }

        @Override
        public Boolean parse(Object value, TslProperty tslProperty) throws IllegalArgumentException {
            return Boolean.parseBoolean(value.toString());
        }
    },
    DATE(7, "日期", LocalDateTime.class) {
        @Override
        public boolean validType(Object value) {
            if (value instanceof LocalDateTime) return true;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime.parse(value.toString(), formatter);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public LocalDateTime parse(Object value, TslProperty tslProperty) throws IllegalArgumentException {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                return LocalDateTime.parse(value.toString(), formatter);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format: " + value);
            }
        }
    },
    STRUCT(8, "结构体", Object.class) {
        @Override
        public boolean validType(Object value) {
            return value instanceof Map<?,?>;
        }

        @Override
        public Object parse(Object value, TslProperty tslProperty) throws IllegalArgumentException {
            //TODO 结构体数据解析暂时不做
            return value;
        }
    };

    @EnumValue
    @JsonValue
    private final int value;
    private final String name;
    private final Class<?> targetType;

    DataTypeEnum(int value, String name, Class<?> targetType) {
        this.value = value;
        this.name = name;
        this.targetType = targetType;
    }

    public abstract <T> T parse(Object value, TslProperty tslProperty) throws IllegalArgumentException;

    public abstract boolean validType(Object value);
}