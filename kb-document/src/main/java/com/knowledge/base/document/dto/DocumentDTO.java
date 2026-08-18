package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 文档DTO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于接收文档创建/更新请求参数</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "文档信息请求参数")
public class DocumentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID", example = "1234567890123456789")
    private Long id;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题", required = true, example = "Spring Boot使用指南")
    @NotBlank(message = "文档标题不能为空")
    @Size(max = 200, message = "文档标题长度不能超过200个字符")
    private String title;

    /**
     * 文档摘要
     */
    @Schema(description = "文档摘要", example = "本文档介绍Spring Boot的基本使用方法")
    @Size(max = 500, message = "文档摘要长度不能超过500个字符")
    private String summary;

    /**
     * 文档内容
     */
    @Schema(description = "文档内容")
    private String content;

    /**
     * 文档类型（1-文章，2-文件）
     */
    @Schema(description = "文档类型（1-文章，2-文件）", example = "1")
    private Integer documentType;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1234567890123456789")
    private Long categoryId;

    /**
     * 团队空间ID
     */
    @Schema(description = "团队空间ID", example = "1234567890123456789")
    private Long teamId;

    /**
     * 标签（逗号分隔）
     */
    @Schema(description = "标签（逗号分隔）", example = "Spring Boot,Java,后端")
    @Size(max = 200, message = "标签长度不能超过200个字符")
    private String tags;

    /**
     * 状态（0-草稿，1-已发布，2-已归档）
     */
    @Schema(description = "状态（0-草稿，1-已发布，2-已归档）", example = "1")
    private Integer status;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶（0-否，1-是）", example = "0")
    private Integer isTop;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐（0-否，1-是）", example = "0")
    private Integer isRecommend;

    /**
     * 封面图URL
     */
    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    @Size(max = 500, message = "封面图URL长度不能超过500个字符")
    private String coverImage;

    /**
     * 来源（1-原创，2-转载，3-翻译）
     */
    @Schema(description = "来源（1-原创，2-转载，3-翻译）", example = "1")
    private Integer source;

    /**
     * 来源URL
     */
    @Schema(description = "来源URL", example = "https://example.com/original-article")
    @Size(max = 500, message = "来源URL长度不能超过500个字符")
    private String sourceUrl;

    /**
     * 允许评论
     */
    @Schema(description = "允许评论（0-否，1-是）", example = "1")
    private Integer allowComment;

    /**
     * 是否公开（0-私有，1-公开）
     */
    @Schema(description = "是否公开（0-私有，1-公开）", example = "1")
    private Integer isPublic;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "0")
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "这是备注信息")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "102400")
    private Long fileSize;

    /**
     * 文件路径（文件上传后的存储URL）
     */
    @Schema(description = "文件路径", example = "http://example.com/files/doc.pdf")
    private String filePath;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名", example = "pdf")
    private String fileExtension;
}
