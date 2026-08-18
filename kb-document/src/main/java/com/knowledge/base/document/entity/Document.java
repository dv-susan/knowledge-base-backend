package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文档实体类
 *
 * <p>按照阿里巴巴Java开发规范设计，存储文档信息</p>
 *
 * <p>注意：content字段存储在MongoDB中，此实体只包含contentId关联字段</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_document")
public class Document extends BaseEntity {

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档摘要
     */
    private String summary;

    /**
     * 文档内容（已废弃，内容存储在MongoDB中）
     * 使用@TableField(exist = false)告诉MyBatis Plus忽略此字段
     */
    @TableField(exist = false)
    private String content;

    /**
     * MongoDB内容ID（关联document_content表的_id）
     */
    private String contentId;

    /**
     * 内容长度（字符数，用于前端字数统计）
     */
    private Integer contentLength;

    /**
     * 文档类型（1-文章，2-文件）
     */
    private Integer documentType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 团队空间ID
     */
    private Long teamId;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

    /**
     * 状态（0-草稿，1-已发布，2-已归档）
     */
    private Integer status;

    /**
     * 是否公开（0-私有，1-公开）
     */
    private Integer isPublic;

    /**
     * 是否置顶（0-否，1-是）
     */
    private Integer isTop;

    /**
     * 是否推荐（0-否，1-是）
     */
    private Integer isRecommend;

    /**
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 点赞次数
     */
    private Long likeCount;

    /**
     * 收藏次数
     */
    private Long favoriteCount;

    /**
     * 评论次数
     */
    private Long commentCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 来源（1-原创，2-转载，3-翻译）
     */
    private Integer source;

    /**
     * 来源URL
     */
    private String sourceUrl;

    /**
     * 允许评论（0-否，1-是）
     */
    private Integer allowComment;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 自动保存草稿已确认（0-未确认，1-用户已放弃恢复）
     */
    private Integer autoSaveDismissed;

    /**
     * 备注
     */
    private String remark;
}
