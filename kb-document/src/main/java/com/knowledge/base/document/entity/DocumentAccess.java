package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档访问记录实体类
 *
 * <p>按照阿里巴巴Java开发规范设计，记录用户访问文档的历史记录</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@TableName("kb_document_access")
public class DocumentAccess {

    /**
     * 访问记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 文档标题
     */
    private String documentTitle;

    /**
     * 访问时间
     */
    private LocalDateTime accessTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 创建人ID
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 更新人ID
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    /**
     * 逻辑删除标记（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 文档摘要（查询时关联获取）
     */
    @TableField(exist = false)
    private String summary;

    /**
     * 分类名称（查询时关联获取）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 作者名称（查询时关联获取）
     */
    @TableField(exist = false)
    private String authorName;

    /**
     * 文档状态（查询时关联获取）
     */
    @TableField(exist = false)
    private Integer status;
}
