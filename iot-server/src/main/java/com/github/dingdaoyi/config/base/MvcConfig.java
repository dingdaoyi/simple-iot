package com.github.dingdaoyi.config.base;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author wo
 * @since 2021/2/24
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass({WebMvcConfigurer.class,WebMvcRegistrations.class})
public class MvcConfig implements WebMvcConfigurer, WebMvcRegistrations {

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * Jackson全局转化long类型为String，解决jackson序列化时long类型缺失精度问题
     *
     * @return Jackson2ObjectMapperBuilderCustomizer 注入的对象
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance)
                    .serializerByType(Long.class, ToStringSerializer.instance)
                    .serializerByType(long.class, ToStringSerializer.instance)
                    .serializerByType(LocalDateTime.class, localDateTimeSerializer())
                    .deserializerByType(LocalDateTime.class, localDateTimeDeserializer())
                    .serializerByType(LocalDate.class, localDateSerializer())
                    .deserializerByType(LocalDate.class, localDateDeserializer())
                    .serializerByType(LocalTime.class, localTimeSerializer())
                    .deserializerByType(LocalTime.class, localTimeDeserializer())
                    .deserializerByType(String.class,stringStdScalarDeserializer());
        };
    }

    @Bean
    public StdScalarDeserializer<String> stringStdScalarDeserializer() {
        return new StdScalarDeserializer<>(String.class) {
            @Override
            public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
                    throws IOException {
                return StringUtils.trimAllWhitespace(jsonParser.getValueAsString());
            }
        };
    }

    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }

    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }

    @Bean
    public LocalDateSerializer localDateSerializer() {
        return new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    @Bean
    public LocalDateDeserializer localDateDeserializer() {
        return new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    @Bean
    public LocalTimeSerializer localTimeSerializer() {
        return new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
    }

    @Bean
    public LocalTimeDeserializer localTimeDeserializer() {
        return new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
    }
    @ControllerAdvice
    public static class ControllerStringParamTrimConfig {

        /**
         * url和form表单中的参数trim
         */
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
            binder.registerCustomEditor(String.class, stringTrimmerEditor);
        }
    }
}
