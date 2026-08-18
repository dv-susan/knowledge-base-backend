package com.knowledge.base.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 操作日志事件 DTO
 *
 * <p>通过 RabbitMQ 传递操作日志数据，由 kb-foundation 消费后写入 kb_operation_log 表</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模块名称 */
    private String module;

    /** 操作类型 */
    private String operationType;

    /** 操作描述 */
    private String operationDesc;

    /** 请求方法 */
    private String requestMethod;

    /** 请求URL */
    private String requestUrl;

    /** 请求参数（JSON） */
    private String requestParams;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** IP地址 */
    private String ipAddress;

    /** 用户代理 */
    private String userAgent;

    /** 执行时长（毫秒） */
    private Integer executeTime;

    /** 状态：1-成功，0-失败 */
    private Integer status;

    /** 错误信息 */
    private String errorMsg;
}
