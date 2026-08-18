package com.knowledge.base.userauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * <p>按照阿里巴巴Java开发规范设计，存储系统用户信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱是否已验证（0-未验证，1-已验证）
     */
    private Integer emailVerified;

    /**
     * 账户激活令牌
     */
    private String activationToken;

    /**
     * 激活令牌过期时间
     */
    private LocalDateTime activationTokenExpiry;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 部门
     */
    @TableField("department")
    private String department;

    /**
     * 岗位
     */
    @TableField("position")
    private String position;

    /**
     * 备注/个人简介
     */
    private String remark;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 是否删除（0-否，1-是）
     */
    @TableField(exist = false)
    private Integer isDeleted;
}
