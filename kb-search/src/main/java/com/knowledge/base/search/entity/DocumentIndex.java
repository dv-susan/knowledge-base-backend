package com.knowledge.base.search.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;

/**
 * 文档索引实体
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "kb_document", createIndex = false)
public class DocumentIndex {

    /**
     * 文档ID
     */
    @Id
    private String id;

    /**
     * 文档标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    /**
     * 文档摘要
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String summary;

    /**
     * 文档内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    /**
     * 分类ID
     */
    @Field(type = FieldType.Long)
    private Long categoryId;

    /**
     * 分类名称
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 标签ID列表
     */
    @Field(type = FieldType.Long)
    private List<Long> tagIds;

    /**
     * 标签名称列表
     */
    @Field(type = FieldType.Keyword)
    private List<String> tagNames;

    /**
     * 创建者ID
     */
    @Field(type = FieldType.Long)
    private Long creatorId;

    /**
     * 创建者名称
     */
    @Field(type = FieldType.Keyword)
    private String creatorName;

    /**
     * 团队ID
     */
    @Field(type = FieldType.Long)
    private Long teamId;

    /**
     * 团队名称
     */
    @Field(type = FieldType.Keyword)
    private String teamName;

    /**
     * 文档状态
     */
    @Field(type = FieldType.Integer)
    private Integer docStatus;

    /**
     * 浏览次数
     */
    @Field(type = FieldType.Integer)
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @Field(type = FieldType.Integer)
    private Integer likeCount;

    /**
     * 评论次数
     */
    @Field(type = FieldType.Integer)
    private Integer commentCount;

    /**
     * 是否公开
     */
    @Field(type = FieldType.Boolean)
    private Boolean isPublic;

    /**
     * 发布时间
     */
    @Field(type = FieldType.Keyword)
    private String publishAt;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Keyword)
    private String createdAt;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Keyword)
    private String updatedAt;
}
