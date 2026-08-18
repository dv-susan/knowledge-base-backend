package com.knowledge.base.common.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.UUID;

/**
 * 实例标识器 — 为多开发者本地环境提供 RabbitMQ 资源隔离
 *
 * <p>同一台机器上运行的所有微服务模块共享同一个 instanceId，
 * 不同机器的 instanceId 相互独立，确保开发者 A 产生的 MQ 消息
 * 只能被开发者 A 的消费者消费。</p>
 *
 * <p>instanceId 的确定优先级：</p>
 * <ol>
 *   <li>{@code app.instance.id} 配置项（显式指定）</li>
 *   <li>本机 hostname（自动检测）</li>
 *   <li>随机 UUID 前 8 位（兜底）</li>
 * </ol>
 *
 * <p>使用方式：将 instanceId 拼接到队列名和路由键中，例如：</p>
 * <pre>
 * 队列名：  kb.notification.review.queue.&#64;{instanceId}
 * 路由键：  notification.review.&#64;{instanceId}.submitted
 * 绑定模式：notification.review.&#64;{instanceId}.*
 * </pre>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class InstanceIdentifier implements InitializingBean {

    /**
     * 显式指定的实例 ID（优先级最高）
     */
    @Value("${app.instance.id:}")
    private String configuredId;

    /**
     * 最终确定的实例 ID
     */
    @Getter
    private String id;

    @Override
    public void afterPropertiesSet() {
        this.id = resolve();
        log.info("当前实例标识（instanceId）：{}", this.id);
    }

    private String resolve() {
        // 1. 显式配置（最高优先级）
        if (configuredId != null && !configuredId.trim().isEmpty()) {
            return sanitize(configuredId.trim());
        }

        // 2. 本机 hostname
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            if (hostname != null && !hostname.isBlank()) {
                // 去掉域名后缀，只保留主机名
                int dotIndex = hostname.indexOf('.');
                if (dotIndex > 0) {
                    hostname = hostname.substring(0, dotIndex);
                }
                return sanitize(hostname);
            }
        } catch (Exception e) {
            log.warn("获取本机 hostname 失败：{}", e.getMessage());
        }

        // 3. 随机 UUID 兜底
        return "local-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 清理非法字符，仅保留字母、数字、连字符、下划线、点号
     */
    private String sanitize(String raw) {
        return raw.replaceAll("[^a-zA-Z0-9._-]", "-");
    }
}
