package com.knowledge.base.userauth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 *
 * <p>按照阿里巴巴Java开发规范设计，配置API文档</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置OpenAPI
     *
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("知识库系统 - 用户权限服务API文档")
                .version("1.0.0")
                .description("提供用户认证、授权、权限管理等功能")
                .contact(new Contact()
                    .name("苏三")
                    .email("support@knowledge-base.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
