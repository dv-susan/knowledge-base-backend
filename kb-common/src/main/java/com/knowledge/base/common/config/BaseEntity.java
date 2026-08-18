package com.knowledge.base.common.config;

import com.baomidou.mybatisplus.annotation.*;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * <p>按照阿里巴巴Java开发规范设计，所有实体类应继承此类</p>
 * <p>包含通用字段：ID、创建时间、更新时间、创建人、更新人、逻辑删除标记</p>
 *
 * <p>设计说明：</p>
 * <ul>
 *   <li>ID：使用雪花算法生成，保证分布式唯一性</li>
 *   <li>逻辑删除：使用@TableLogic注解，MyBatis Plus自动处理</li>
 *   <li>字段自动填充：使用FieldFill，配合MetaObjectHandler实现</li>
 *   <li>不包含version字段：乐观锁不是所有表的必需功能，按需添加</li>
 * </ul>
 *
 * <p>如果需要乐观锁：</p>
 * <ul>
 *   <li>方式1：在具体实体类中添加@Version private Integer version字段</li>
 *   <li>方式2：继承BaseEntityWithVersion类（推荐）</li>
 *   <li>注意：数据库表需要添加version字段（INT，默认值0）</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(type = IdType.INPUT)
    private Long id;

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
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 逻辑删除标记（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 插入前自动填充ID
     */
    public void preInsert() {
        if (this.id == null) {
            this.id = SnowflakeIdGenerator.getInstance().nextId();
        }
    }
}
