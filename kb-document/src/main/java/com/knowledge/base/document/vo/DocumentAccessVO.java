package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档访问记录响应VO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于返回文档访问记录信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "文档访问记录响应")
public class DocumentAccessVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问记录ID
     */
    @Schema(description = "访问记录ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID")
    private Long documentId;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题")
    private String documentTitle;

    /**
     * 文档摘要
     */
    @Schema(description = "文档摘要")
    private String summary;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 作者名称
     */
    @Schema(description = "作者名称")
    private String authorName;

    /**
     * 访问时间
     */
    @Schema(description = "访问时间")
    private LocalDateTime accessTime;

    /**
     * 文档状态
     */
    @Schema(description = "文档状态")
    private Integer status;
}
