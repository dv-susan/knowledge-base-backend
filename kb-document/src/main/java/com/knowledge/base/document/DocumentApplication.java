package com.knowledge.base.document;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.knowledge.base.document", "com.knowledge.base.common"})
@MapperScan("com.knowledge.base.document.mapper")
@ServletComponentScan(basePackages = "com.knowledge.base.document.filter")
@EnableFeignClients(basePackages = {"com.knowledge.base.document.feign", "com.knowledge.base.common.feign"})
@EnableScheduling
public class DocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentApplication.class, args);
        System.out.println("========================================");
        System.out.println("文档服务启动成功！");
        System.out.println("Swagger文档地址: http://localhost:8082/api/document/doc.html");
        System.out.println("========================================");
    }
}