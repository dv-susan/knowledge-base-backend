package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档审核记录实体
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@TableName("tb_document_review")
@Schema(description = "文档审核记录实体")
public class DocumentReview {

    /**
     * 审核记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "审核记录ID")
    private Long id;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID")
    private Long documentId;

    /**
     * 审核人ID
     */
    @Schema(description = "审核人ID")
    private Long reviewerId;

    /**
     * 审核人姓名
     */
    @Schema(description = "审核人姓名")
    private String reviewerName;

    /**
     * 审核结果：1-通过，2-驳回
     */
    @Schema(description = "审核结果")
    private Integer reviewResult;

    /**
     * 审核意见
     */
    @Schema(description = "审核意见")
    private String reviewComment;

    /**
     * 审核前状态
     */
    @Schema(description = "审核前状态")
    private Integer beforeStatus;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime reviewedAt;

    /**
     * 审核轮次
     */
    @Schema(description = "审核轮次")
    private Integer reviewRound;

    /**
     * 审核级别（1=一级审核，预留多级扩展）
     */
    @Schema(description = "审核级别")
    private Integer reviewLevel;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
