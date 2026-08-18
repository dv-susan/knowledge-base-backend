package com.knowledge.base.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

/**
 * Jackson自动配置
 *
 * <p>确保Jackson能够正确序列化和反序列化对象，并解决JavaScript大整数精度丢失问题</p>
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>Long类型序列化为字符串：解决JavaScript无法安全表示超过2^53-1的大整数问题</li>
 *   <li>支持Java 8日期时间类型：LocalDateTime、LocalDate等</li>
 *   <li>忽略未知属性：避免反序列化时因未知字段报错</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class JacksonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        // 添加Java 8日期时间模块支持
        builder.modules(new JavaTimeModule());

        // 设置日期格式
        builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 配置序列化特性
        builder.featuresToDisable(
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        );

        // 配置自定义序列化器
        builder.serializerByType(Long.class, ToStringSerializer.instance);

        return builder;
    }
}
