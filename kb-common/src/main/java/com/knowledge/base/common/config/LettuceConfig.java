package com.knowledge.base.common.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

/**
 * Lettuce Redis 客户端配置
 *
 * 解决远程 Redis 间歇性超时问题：
 * 1. TCP KeepAlive - 防止中间网络设备（防火墙/NAT/负载均衡）回收空闲连接
 * 2. pingBeforeActivateConnection - 从连接池取出连接时先 PING 验证可用性
 * 3. autoReconnect - 连接断开后自动重连
 * 4. 超时配置 - TCP 连接超时和命令超时分离
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(LettuceConnectionFactory.class)
public class LettuceConfig {

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return builder -> {

            // TCP KeepAlive: 每60s空闲后发探测包，30s间隔，3次失败断开
            SocketOptions.KeepAliveOptions keepAliveOptions = SocketOptions.KeepAliveOptions.builder()
                    .enable(true)
                    .idle(Duration.ofSeconds(60))
                    .interval(Duration.ofSeconds(30))
                    .count(3)
                    .build();

            SocketOptions socketOptions = SocketOptions.builder()
                    .connectTimeout(Duration.ofSeconds(3))   // TCP 握手超时 3s
                    .keepAlive(keepAliveOptions)
                    .tcpNoDelay(true)                         // 禁用 Nagle 算法
                    .build();

            ClientOptions clientOptions = ClientOptions.builder()
                    .autoReconnect(true)                      // 自动重连
                    .pingBeforeActivateConnection(true)       // 使用连接前 PING 验证
                    .socketOptions(socketOptions)
                    .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(10))) // 命令超时 10s
                    .build();

            builder.clientOptions(clientOptions);
        };
    }
}
