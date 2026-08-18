package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档响应VO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于返回文档信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "文档信息响应")
public class DocumentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID")
    private Long id;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题")
    private String title;

    /**
     * 文档摘要
     */
    @Schema(description = "文档摘要")
    private String summary;

    /**
     * 文档内容
     */
    @Schema(description = "文档内容")
    private String content;

    /**
     * 内容长度（字符数）
     */
    @Schema(description = "内容长度（字符数）")
    private Integer contentLength;

    /**
     * 文档类型（1-文章，2-文件）
     */
    @Schema(description = "文档类型")
    private Integer documentType;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 团队空间ID
     */
    @Schema(description = "团队空间ID")
    private Long teamId;

    /**
     * 团队空间名称
     */
    @Schema(description = "团队空间名称")
    private String teamName;

    /**
     * 标签列表
     */
    @Schema(description = "标签列表")
    private String tags;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否公开
     */
    @Schema(description = "是否公开")
    private Integer isPublic;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶")
    private Integer isTop;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐")
    private Integer isRecommend;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Long viewCount;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数")
    private Long likeCount;

    /**
     * 当前用户是否已点赞
     */
    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;

    /**
     * 收藏次数
     */
    @Schema(description = "收藏次数")
    private Long favoriteCount;

    /**
     * 评论次数
     */
    @Schema(description = "评论次数")
    private Long commentCount;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    /**
     * 作者ID
     */
    @Schema(description = "作者ID")
    @Deprecated
    private Long authorId;

    /**
     * 作者名称
     */
    @Schema(description = "作者名称")
    @Deprecated
    private String authorName;

    /**
     * 作者信息
     */
    @Schema(description = "作者信息")
    private AuthorVO author;

    /**
     * 封面图URL
     */
    @Schema(description = "封面图URL")
    private String coverImage;

    /**
     * 来源
     */
    @Schema(description = "来源")
    private Integer source;

    /**
     * 来源URL
     */
    @Schema(description = "来源URL")
    private String sourceUrl;

    /**
     * 允许评论
     */
    @Schema(description = "允许评论")
    private Integer allowComment;

    /**
     * 自动保存提示是否已关闭
     * <p>null=非自动保存创建的文档, 0=自动保存创建且未关闭提示, 1=已关闭提示</p>
     */
    @Schema(description = "自动保存提示是否已关闭（null=非自动保存, 0=未关闭提示, 1=已关闭提示）")
    private Integer autoSaveDismissed;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
