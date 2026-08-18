package com.knowledge.base.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 重建索引进度VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "重建索引进度")
public class ReindexProgressVO {

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "任务状态：RUNNING | COMPLETED | FAILED | NOT_FOUND")
    private String status;

    @Schema(description = "文档总数")
    private int totalDocuments;

    @Schema(description = "已完成数")
    private int completedDocuments;

    @Schema(description = "失败数")
    private int failedDocuments;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    private LocalDateTime endTime;
}
