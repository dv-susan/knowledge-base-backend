package com.knowledge.base.foundation.dto;

import com.knowledge.base.common.result.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 操作日志查询DTO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于操作日志查询条件</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "操作日志查询参数")
public class OperationLogQueryDTO extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模块名称
     */
    @Schema(description = "模块名称", example = "用户管理")
    private String module;

    /**
     * 操作类型：LOGIN/CREATE/UPDATE/DELETE等
     */
    @Schema(description = "操作类型", example = "CREATE")
    private String operationType;

    /**
     * 操作用户ID
     */
    @Schema(description = "操作用户ID", example = "1234567890123456789")
    private Long userId;

    /**
     * 操作用户名
     */
    @Schema(description = "操作用户名", example = "admin")
    private String username;

    /**
     * 状态：0-失败，1-成功
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private String endTime;

    /**
     * 关键词搜索（操作描述或请求URL）
     */
    @Schema(description = "关键词", example = "创建")
    private String keyword;
}
