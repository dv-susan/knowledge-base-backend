package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档分类实体类
 *
 * <p>按照阿里巴巴Java开发规范设计，存储文档分类信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_category")
public class Category extends BaseEntity {

    /**
     * 父分类ID（0表示根分类）
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 图标
     */
    @TableField("category_icon")
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 文档数量
     */
    private Integer documentCount;

}
