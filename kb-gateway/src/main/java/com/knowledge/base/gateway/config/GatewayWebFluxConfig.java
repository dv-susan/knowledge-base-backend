package com.knowledge.base.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * 网关WebFlux配置
 *
 * <p>确保网关能够正确处理JSON响应体</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class GatewayWebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        // 配置消息编码器，确保响应体能够正确处理
        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024); // 16MB
    }
}
