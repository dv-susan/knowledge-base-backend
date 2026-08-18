package com.knowledge.base.userauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户DTO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于接收用户创建/更新请求参数</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "用户信息请求参数")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1234567890123456789")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", required = true, example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "123456")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 昵称
     */
    @Schema(description = "昵称", example = "张三")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    @Schema(description = "性别（0-未知，1-男，2-女）", example = "1")
    private Integer gender;

    /**
     * 状态（0-禁用，1-启用）
     */
    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "这是备注信息")
    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;

    /**
     * 部门
     */
    @Schema(description = "部门", example = "技术部")
    @Size(max = 50, message = "部门长度不能超过50个字符")
    private String department;

    /**
     * 岗位
     */
    @Schema(description = "岗位", example = "Java开发工程师")
    @Size(max = 50, message = "岗位长度不能超过50个字符")
    private String position;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1234567890123456789")
    private Long deptId;

    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID", example = "1234567890123456789")
    private Long postId;
}
