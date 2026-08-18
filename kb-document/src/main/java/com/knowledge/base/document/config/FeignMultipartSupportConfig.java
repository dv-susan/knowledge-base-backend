package com.knowledge.base.document.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 多部分文件上传配置
 *
 * <p>配置 SpringFormEncoder 以支持通过 Feign 客户端上传 MultipartFile</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class FeignMultipartSupportConfig {

    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignMultipartSupportConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
}
