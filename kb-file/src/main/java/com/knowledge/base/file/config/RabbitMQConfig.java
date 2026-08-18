package com.knowledge.base.file.config;

import com.knowledge.base.common.config.InstanceIdentifier;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置
 * 用于异步转码任务的分发与消费
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class RabbitMQConfig {

    public static final String TRANSCODE_EXCHANGE = "transcode.exchange";

    @Resource
    private InstanceIdentifier instanceIdentifier;

    @Bean
    public DirectExchange transcodeExchange() {
        return new DirectExchange(TRANSCODE_EXCHANGE, true, false);
    }

    @Bean
    public Queue transcodeQueue() {
        return new Queue("transcode.queue." + instanceIdentifier.getId(), true, false, false);
    }

    @Bean
    public Binding transcodeBinding() {
        return BindingBuilder.bind(transcodeQueue())
                .to(transcodeExchange())
                .with("transcode." + instanceIdentifier.getId());
    }

    /**
     * 返回实例作用域的转码队列名称
     * 供 {@code @RabbitListener} 的 SpEL 表达式引用
     */
    public String transcodeQueueName() {
        return "transcode.queue." + instanceIdentifier.getId();
    }
}
