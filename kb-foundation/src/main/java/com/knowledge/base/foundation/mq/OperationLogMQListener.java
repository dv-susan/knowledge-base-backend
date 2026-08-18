package com.knowledge.base.foundation.mq;

import com.knowledge.base.common.event.OperationLogEventDTO;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 操作日志 MQ 监听器
 *
 * <p>消费来自各业务服务的操作日志事件，写入 kb_operation_log 表</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class OperationLogMQListener {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 消费操作日志事件
     */
    @RabbitListener(queues = "#{@operationLogQueue.name}")
    public void handleOperationLogEvent(OperationLogEventDTO event) {
        try {
            log.debug("收到操作日志事件：module={}, operationType={}, userId={}",
                    event.getModule(), event.getOperationType(), event.getUserId());

            long id = SnowflakeIdGenerator.getInstance().nextId();
            jdbcTemplate.update(
                    "INSERT INTO kb_operation_log " +
                            "(id, module, operation_type, operation_desc, request_method, request_url, " +
                            "request_params, user_id, username, ip_address, user_agent, " +
                            "execute_time, status, error_msg, created_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())",
                    id,
                    event.getModule(),
                    event.getOperationType(),
                    event.getOperationDesc(),
                    event.getRequestMethod(),
                    event.getRequestUrl(),
                    event.getRequestParams(),
                    event.getUserId(),
                    event.getUsername(),
                    event.getIpAddress(),
                    event.getUserAgent(),
                    event.getExecuteTime(),
                    event.getStatus(),
                    event.getErrorMsg()
            );

            log.debug("操作日志写入成功：id={}, module={}, operationType={}", id, event.getModule(), event.getOperationType());
        } catch (Exception e) {
            log.error("处理操作日志事件失败：module={}, operationType={}, userId={}, error={}",
                    event.getModule(), event.getOperationType(), event.getUserId(), e.getMessage(), e);
            // 不抛出异常，避免消息无限重试
        }
    }
}
