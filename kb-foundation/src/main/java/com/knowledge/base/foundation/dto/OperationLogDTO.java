package com.knowledge.base.foundation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 操作日志DTO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于接收操作日志创建请求参数</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "操作日志请求参数")
public class OperationLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @Schema(description = "日志ID", example = "1234567890123456789")
    private Long id;

    /**
     * 模块名称
     */
    @Schema(description = "模块名称", required = true, example = "用户管理")
    @NotBlank(message = "模块名称不能为空")
    @Size(max = 50, message = "模块名称长度不能超过50个字符")
    private String module;

    /**
     * 操作类型：LOGIN/CREATE/UPDATE/DELETE等
     */
    @Schema(description = "操作类型", required = true, example = "CREATE")
    @NotBlank(message = "操作类型不能为空")
    @Size(max = 20, message = "操作类型长度不能超过20个字符")
    private String operationType;

    /**
     * 操作描述
     */
    @Schema(description = "操作描述", required = true, example = "创建用户")
    @NotBlank(message = "操作描述不能为空")
    @Size(max = 500, message = "操作描述长度不能超过500个字符")
    private String operationDesc;

    /**
     * 请求方法：GET/POST/PUT/DELETE
     */
    @Schema(description = "请求方法", example = "POST")
    @Size(max = 10, message = "请求方法长度不能超过10个字符")
    private String requestMethod;

    /**
     * 请求URL
     */
    @Schema(description = "请求URL", example = "/api/users")
    @Size(max = 500, message = "请求URL长度不能超过500个字符")
    private String requestUrl;

    /**
     * 请求参数（JSON）
     */
    @Schema(description = "请求参数", example = "{\"username\":\"zhangsan\"}")
    private String requestParams;

    /**
     * 响应结果（JSON）
     */
    @Schema(description = "响应结果", example = "{\"code\":200,\"message\":\"success\"}")
    private String responseResult;

    /**
     * 操作用户ID
     */
    @Schema(description = "操作用户ID", example = "1234567890123456789")
    private Long userId;

    /**
     * 操作用户名
     */
    @Schema(description = "操作用户名", example = "admin")
    @Size(max = 50, message = "操作用户名长度不能超过50个字符")
    private String username;

    /**
     * IP地址
     */
    @Schema(description = "IP地址", example = "192.168.1.1")
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    private String ipAddress;

    /**
     * 地理位置
     */
    @Schema(description = "地理位置", example = "北京市")
    @Size(max = 100, message = "地理位置长度不能超过100个字符")
    private String location;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理", example = "Mozilla/5.0...")
    @Size(max = 500, message = "用户代理长度不能超过500个字符")
    private String userAgent;

    /**
     * 执行时长（毫秒）
     */
    @Schema(description = "执行时长（毫秒）", example = "100")
    private Integer executeTime;

    /**
     * 状态：0-失败，1-成功
     */
    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息", example = "操作失败")
    @Size(max = 2000, message = "错误信息长度不能超过2000个字符")
    private String errorMsg;
}
