package com.github.dingdaoyi.proto.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author dingyunwei
 */

@Getter
public enum DataTypeEnum {
    INT(1, Integer.class) {
        @Override
        public Integer parse(Object value) throws IllegalArgumentException {
            return Integer.parseInt(value.toString());
        }
    },
    FLOAT(2, Float.class) {
        @Override
        public Float parse(Object value) throws IllegalArgumentException {
            return Float.parseFloat(value.toString());
        }
    },
    DOUBLE(3, Double.class) {
        @Override
        public Double parse(Object value) throws IllegalArgumentException {
            return Double.parseDouble(value.toString());
        }
    },
    ENUM(4, String.class) {
        @Override
        public String parse(Object value) throws IllegalArgumentException {
            return value.toString();
        }
    },
    TEXT(5, String.class) {
        @Override
        public String parse(Object value) throws IllegalArgumentException {
            return value.toString();
        }
    },
    BOOL(6, Boolean.class) {
        @Override
        public Boolean parse(Object value) throws IllegalArgumentException {
            return Boolean.parseBoolean(value.toString());
        }
    },
    DATE(7, LocalDateTime.class) {
        @Override
        public LocalDateTime parse(Object value) throws IllegalArgumentException {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                 return LocalDateTime.parse(value.toString(), formatter);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format: " + value);
            }
        }
    },
    STRUCT(8, Object.class) {
        @Override
        public Object parse(Object value) throws IllegalArgumentException {
            return value;
        }
    };

    @EnumValue
    @JsonValue
    private final int value;
    private final Class<?> targetType;

    DataTypeEnum(int value, Class<?> targetType) {
        this.value = value;
        this.targetType = targetType;
    }

    public abstract <T> T parse(Object value) throws IllegalArgumentException;
}