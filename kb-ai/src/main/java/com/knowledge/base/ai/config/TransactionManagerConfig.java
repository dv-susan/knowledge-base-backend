package com.knowledge.base.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 事务管理器配置
 *
 * <p>解决 kb-ai 模块同时引入 MyBatis-Plus 和 spring-data-neo4j 时，
 * {@code @Transactional} 注解默认路由到 Neo4jTransactionManager 的问题。
 * 将 JDBC DataSourceTransactionManager 标记为 @Primary，确保所有
 * 不显式指定 transactionManager 的数据库操作使用 JDBC 事务。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class TransactionManagerConfig {

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
