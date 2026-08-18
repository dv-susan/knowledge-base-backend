package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_user_favorite")
public class UserFavorite extends BaseEntity {

    /**
     * 重写 deleted 字段，禁用 BaseEntity 的逻辑删除
     * 收藏功能使用物理删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 文档标题（冗余字段）
     */
    private String documentTitle;

    /**
     * 文档分类ID（冗余字段）
     */
    private Long documentCategoryId;

    /**
     * 收藏时间
     */
    private LocalDateTime favoriteTime;

    /**
     * 文档摘要（关联查询字段，不存储在数据库）
     */
    @TableField(exist = false)
    private String documentSummary;

    /**
     * 文档作者名称（关联查询字段，不存储在数据库）
     */
    @TableField(exist = false)
    private String documentAuthorName;

    /**
     * 文档作者ID（关联查询字段，不存储在数据库）
     */
    @TableField(exist = false)
    private Long documentAuthorId;

    /**
     * 文档状态（关联查询字段，不存储在数据库）
     */
    @TableField(exist = false)
    private Integer documentStatus;

    /**
     * 文档浏览次数（关联查询字段，不存储在数据库）
     */
    @TableField(exist = false)
    private Long documentViewCount;

    /**
     * 文档创建时间（关联查询字段，不存储在数据库）
     */
    @TableField(exist = false)
    private LocalDateTime documentCreateTime;

    /**
     * 文档更新时间（关联查询字段，不存储在数据库）
     */
    @TableField(exist = false)
    private LocalDateTime documentUpdateTime;
}
