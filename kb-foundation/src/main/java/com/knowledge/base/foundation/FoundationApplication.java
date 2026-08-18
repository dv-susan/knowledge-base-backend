package com.knowledge.base.foundation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * kb-foundation基础服务启动类
 *
 * @author 苏三
 * @since 1.0.0
 */
@EnableAsync
@EnableCaching
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan(basePackages = {"com.knowledge.base.foundation", "com.knowledge.base.common"})
@EnableFeignClients(basePackages = "com.knowledge.base.foundation.feign")
public class FoundationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoundationApplication.class, args);
        System.out.println("""

                ========================================
                   基础服务启动成功！
                   服务名称: kb-foundation
                   服务端口: 8089
                   API文档: http://localhost:8089/api/foundation/doc.html
                   Druid监控: http://localhost:8089/api/foundation/druid/
                ========================================
                """);
    }
}