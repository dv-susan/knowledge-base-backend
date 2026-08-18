package com.knowledge.base.userauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 *
 * <p>按照阿里巴巴Java开发规范设计，存储系统权限信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_permission")
public class Permission extends BaseEntity {

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型（1-菜单，2-按钮）
     */
    private Integer permissionType;

    /**
     * 菜单URL
     */
    @TableField("menu_url")
    private String menuUrl;

    /**
     * 接口URL
     */
    @TableField("api_url")
    private String apiUrl;

    /**
     * 请求方法
     */
    @TableField("method")
    private String method;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
}
