package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "用户收藏信息")
public class UserFavoriteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @Schema(description = "收藏ID")
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
    private String documentSummary;

    /**
     * 文档分类ID
     */
    @Schema(description = "文档分类ID")
    private Long documentCategoryId;

    /**
     * 文档分类名称
     */
    @Schema(description = "文档分类名称")
    private String documentCategoryName;

    /**
     * 文档作者ID
     */
    @Schema(description = "文档作者ID")
    private Long documentAuthorId;

    /**
     * 文档作者名称
     */
    @Schema(description = "文档作者名称")
    private String documentAuthorName;

    /**
     * 文档状态
     */
    @Schema(description = "文档状态")
    private Integer documentStatus;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Long documentViewCount;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间")
    private LocalDateTime favoriteTime;

    /**
     * 是否已收藏
     */
    @Schema(description = "是否已收藏")
    private Boolean isFavorited;
}
