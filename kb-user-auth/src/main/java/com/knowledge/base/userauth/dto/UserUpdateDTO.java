package com.knowledge.base.userauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatarUrl;

    /**
     * 部门
     */
    @Schema(description = "部门")
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    private String department;

    /**
     * 职位
     */
    @Schema(description = "职位")
    @Size(max = 100, message = "职位名称长度不能超过100个字符")
    private String position;

    /**
     * 个人简介
     */
    @Schema(description = "个人简介")
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;

    /**
     * 用户类型
     */
    @Schema(description = "用户类型")
    private Integer userType;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
