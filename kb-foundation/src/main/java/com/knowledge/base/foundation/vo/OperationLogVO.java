package com.knowledge.base.foundation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志VO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于返回操作日志信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志响应")
public class OperationLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @Schema(description = "日志ID")
    private Long id;

    /**
     * 模块名称
     */
    @Schema(description = "模块名称")
    private String module;

    /**
     * 操作类型：LOGIN/CREATE/UPDATE/DELETE等
     */
    @Schema(description = "操作类型")
    private String operationType;

    /**
     * 操作描述
     */
    @Schema(description = "操作描述")
    private String operationDesc;

    /**
     * 请求方法：GET/POST/PUT/DELETE
     */
    @Schema(description = "请求方法")
    private String requestMethod;

    /**
     * 请求URL
     */
    @Schema(description = "请求URL")
    private String requestUrl;

    /**
     * 请求参数（JSON）
     */
    @Schema(description = "请求参数")
    private String requestParams;

    /**
     * 响应结果（JSON）
     */
    @Schema(description = "响应结果")
    private String responseResult;

    /**
     * 操作用户ID
     */
    @Schema(description = "操作用户ID")
    private Long userId;

    /**
     * 操作用户名
     */
    @Schema(description = "操作用户名")
    private String username;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 地理位置
     */
    @Schema(description = "地理位置")
    private String location;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    private String userAgent;

    /**
     * 执行时长（毫秒）
     */
    @Schema(description = "执行时长（毫秒）")
    private Integer executeTime;

    /**
     * 状态：0-失败，1-成功
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private LocalDateTime createdAt;
}
