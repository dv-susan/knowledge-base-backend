package com.knowledge.base.ai.config;

import com.knowledge.base.common.config.InstanceIdentifier;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RAG RabbitMQ 配置
 *
 * <p>定义重建索引任务所需的队列、交换机和绑定关系。
 * 使用 Jackson2JsonMessageConverter 进行消息序列化。
 * 队列名和路由键通过 InstanceIdentifier 进行实例隔离，
 * 确保多开发者本地环境的消息互不干扰。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "rag.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitConfig {

    @Resource
    private InstanceIdentifier instanceIdentifier;

    public static final String EXCHANGE = "rag.reindex.exchange";

    public String ragReindexQueueName() {
        return "rag.reindex.queue." + instanceIdentifier.getId();
    }

    public String ragReindexRoutingKeyAll() {
        return "rag.reindex." + instanceIdentifier.getId() + ".all";
    }

    public String ragReindexRoutingKeyByIds() {
        return "rag.reindex." + instanceIdentifier.getId() + ".by_ids";
    }

    public String ragReindexRoutingKeyDelete() {
        return "rag.reindex." + instanceIdentifier.getId() + ".delete";
    }

    @Bean
    public TopicExchange ragReindexExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue ragReindexQueue() {
        return QueueBuilder.durable(ragReindexQueueName())
                .withArgument("x-dead-letter-exchange", EXCHANGE + ".dlx")
                .build();
    }

    @Bean
    public Binding ragReindexAllBinding() {
        return BindingBuilder.bind(ragReindexQueue())
                .to(ragReindexExchange())
                .with(ragReindexRoutingKeyAll());
    }

    @Bean
    public Binding ragReindexByIdsBinding() {
        return BindingBuilder.bind(ragReindexQueue())
                .to(ragReindexExchange())
                .with(ragReindexRoutingKeyByIds());
    }

    @Bean
    public Binding ragReindexDeleteBinding() {
        return BindingBuilder.bind(ragReindexQueue())
                .to(ragReindexExchange())
                .with(ragReindexRoutingKeyDelete());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }
}
