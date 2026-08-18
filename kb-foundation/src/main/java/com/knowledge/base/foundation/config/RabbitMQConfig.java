package com.knowledge.base.foundation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knowledge.base.common.config.InstanceIdentifier;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置
 *
 * <p>队列名与路由键使用 InstanceIdentifier 进行实例隔离，
 * 确保多开发者本地环境的消息互不干扰，
 * 自己电脑产生的 MQ 消息只被自己电脑的消费者消费。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class RabbitMQConfig {

    @Resource
    private InstanceIdentifier instanceIdentifier;

    // ======================== 交换机声明（全局共享） ========================

    /** 通知交换机 */
    public static final String NOTIFICATION_EXCHANGE = "kb.notification.exchange";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    // ======================== 通用通知队列（实例隔离） ========================

    public String notificationQueueName() {
        return "kb.notification.queue." + instanceIdentifier.getId();
    }

    public String notificationRoutingKey() {
        return "notification." + instanceIdentifier.getId() + ".#";
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueueName()).build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(notificationRoutingKey());
    }

    // ======================== 审核通知队列（实例隔离） ========================

    public String reviewNotificationQueueName() {
        return "kb.notification.review.queue." + instanceIdentifier.getId();
    }

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

    // ======================== 系统配置队列（实例隔离） ========================

    /** 系统配置交换机 */
    public static final String CONFIG_EXCHANGE = "kb.config.exchange";

    @Bean
    public DirectExchange configExchange() {
        return new DirectExchange(CONFIG_EXCHANGE, true, false);
    }

    public String configQueueName() {
        return "kb.config.queue." + instanceIdentifier.getId();
    }

    public String configRoutingKey() {
        return "config." + instanceIdentifier.getId() + ".update";
    }

    @Bean
    public Queue configQueue() {
        return QueueBuilder.durable(configQueueName()).build();
    }

    @Bean
    public Binding configBinding() {
        return BindingBuilder.bind(configQueue())
                .to(configExchange())
                .with(configRoutingKey());
    }

    // ======================== 操作日志队列（实例隔离） ========================

    /** 操作日志交换机 */
    public static final String OPERATION_LOG_EXCHANGE = "kb.operationlog.exchange";

    @Bean
    public TopicExchange operationLogExchange() {
        return new TopicExchange(OPERATION_LOG_EXCHANGE, true, false);
    }

    public String operationLogQueueName() {
        return "kb.operationlog.queue." + instanceIdentifier.getId();
    }

    public String operationLogRoutingKey() {
        return "operationlog." + instanceIdentifier.getId() + ".#";
    }

    @Bean
    public Queue operationLogQueue() {
        return QueueBuilder.durable(operationLogQueueName()).build();
    }

    @Bean
    public Binding operationLogBinding() {
        return BindingBuilder.bind(operationLogQueue())
                .to(operationLogExchange())
                .with(operationLogRoutingKey());
    }

    // ======================== Message Converter ========================

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages(
                "com.knowledge.base.common.event",
                "com.knowledge.base.foundation.dto",
                "java.util",
                "java.lang"
        );
        converter.setClassMapper(classMapper);
        return converter;
    }

    // ======================== RabbitTemplate ========================

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
