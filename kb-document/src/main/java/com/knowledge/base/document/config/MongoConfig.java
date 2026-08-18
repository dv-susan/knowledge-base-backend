package com.knowledge.base.document.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB配置类
 *
 * <p>按照苏三配置MongoDB连接</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.knowledge.base.document.repository.mongodb")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host:localhost}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port:27017}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database:knowledge_base}")
    private String databaseName;

    @Value("${spring.data.mongodb.username:}")
    private String username;

    @Value("${spring.data.mongodb.password:}")
    private String password;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        // 构建MongoDB连接URI
        StringBuilder mongoUri = new StringBuilder("mongodb://");

        // 添加认证信息（如果配置了用户名和密码）
        if (username != null && !username.isEmpty()) {
            mongoUri.append(username).append(":").append(password).append("@");
        }

        // 添加主机和端口
        mongoUri.append(mongoHost).append(":").append(mongoPort);

        // 添加数据库名
        mongoUri.append("/").append(databaseName);

        // 添加认证源和连接选项
        mongoUri.append("?authSource=admin");
        mongoUri.append("&connectTimeoutMS=30000");
        mongoUri.append("&socketTimeoutMS=60000");
        mongoUri.append("&serverSelectionTimeoutMS=30000");
        // MongoDB默认使用UTF-8编码，不需要额外配置

        System.out.println("连接MongoDB： " + mongoUri.toString());

        return MongoClients.create(mongoUri.toString());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
