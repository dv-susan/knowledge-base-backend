package com.knowledge.base.foundation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_operation_log")
@Schema(description = "操作日志实体")
public class OperationLog extends BaseEntity {

    @Schema(description = "模块名称")
    @TableField("module")
    private String module;

    @Schema(description = "操作类型：LOGIN/CREATE/UPDATE/DELETE等")
    @TableField("operation_type")
    private String operationType;

    @Schema(description = "操作描述")
    @TableField("operation_desc")
    private String operationDesc;

    @Schema(description = "请求方法：GET/POST/PUT/DELETE")
    @TableField("request_method")
    private String requestMethod;

    @Schema(description = "请求URL")
    @TableField("request_url")
    private String requestUrl;

    @Schema(description = "请求参数（JSON）")
    @TableField("request_params")
    private String requestParams;

    @Schema(description = "响应结果（JSON）")
    @TableField("response_result")
    private String responseResult;

    @Schema(description = "操作用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "操作用户名")
    @TableField("username")
    private String username;

    @Schema(description = "IP地址")
    @TableField("ip_address")
    private String ipAddress;

    @Schema(description = "地理位置")
    @TableField("location")
    private String location;

    @Schema(description = "用户代理")
    @TableField("user_agent")
    private String userAgent;

    @Schema(description = "执行时长（毫秒）")
    @TableField("execute_time")
    private Integer executeTime;

    @Schema(description = "状态：0-失败，1-成功")
    @TableField("status")
    private Integer status;

    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

}
