package com.knowledge.base.document.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knowledge.base.common.config.InstanceIdentifier;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息序列化与交换机声明配置
 *
 * <p>覆盖 Spring Boot 默认的 {@link RabbitAutoConfiguration} 中 SimpleMessageConverter，
 * 使用 Jackson2Json 序列化，确保消息以 JSON 格式发送。</p>
 * <p>配置 DefaultClassMapper 确保发送端在消息头中添加 __TypeId__，
 * 消费者 kb-foundation 才能正确反序列化为目标类型。</p>
 * <p>同时声明通知交换机、队列及绑定，确保 kb-document 发布消息时交换机/队列已存在，
 * 避免因 kb-foundation 尚未启动导致消息被静默丢弃。</p>
 * <p>队列名与路由键使用 InstanceIdentifier 进行实例隔离，确保多开发者本地环境
 * 互不干扰。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    @Resource
    private InstanceIdentifier instanceIdentifier;

    // ======================== 交换机声明（全局共享） ========================

    /** 通知交换机（所有实例共享同一个 TopicExchange） */
    public static final String NOTIFICATION_EXCHANGE = "kb.notification.exchange";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    // ======================== 审核通知队列声明（实例隔离） ========================

    /** 实例隔离后的审核通知队列名（暴露给 StatisticsEventPublisher 等组件使用） */
    public String reviewNotificationQueueName() {
        return "kb.notification.review.queue." + instanceIdentifier.getId();
    }

    /** 实例隔离后的审核通知绑定路由键模式（匹配 submitted / approved / rejected） */
    public String reviewNotificationRoutingKey() {
        return "notification.review." + instanceIdentifier.getId() + ".*";
    }

    @Bean
    public Queue reviewNotificationQueue() {
        return QueueBuilder.durable(reviewNotificationQueueName()).build();
    }

    @Bean
    public Binding reviewNotificationBinding() {
        return BindingBuilder.bind(reviewNotificationQueue())
                .to(notificationExchange())
                .with(reviewNotificationRoutingKey());
    }

    // ======================== 消息转换器 ========================

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages(
                "com.knowledge.base.common.event",
                "com.knowledge.base.document.dto",
                "java.util",
                "java.lang"
        );
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        // 启用发布确认回调，检测消息是否成功到达交换机
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack && correlationData != null) {
                log.error("消息发送到交换机失败：id={}, cause={}", correlationData.getId(),
                        cause != null ? cause : "交换机不存在或不可达");
            }
        });

        // 启用 mandatory 模式，检测消息是否成功路由到队列
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息未路由到任何队列：exchange={}, routingKey={}, replyCode={}, replyText={}",
                    returned.getExchange(), returned.getRoutingKey(),
                    returned.getReplyCode(), returned.getReplyText());
        });

        return rabbitTemplate;
    }
}
