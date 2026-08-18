package com.knowledge.base.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 *
 * <p>按照阿里巴巴Java开发规范设计，配置允许跨域访问</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * 构建CorsFilter
     *
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许携带Cookie
        config.setAllowCredentials(true);

        // 允许所有来源（生产环境建议配置具体域名）
        config.addAllowedOriginPattern("*");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有请求方法
        config.addAllowedMethod("*");

        // 暴露的响应头
        config.addExposedHeader("Content-Disposition");

        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);

        // 对所有路径应用跨域配置
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
