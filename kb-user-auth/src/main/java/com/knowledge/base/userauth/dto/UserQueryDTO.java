package com.knowledge.base.userauth.dto;

import com.knowledge.base.common.result.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询请求")
public class UserQueryDTO extends PageParam {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名（模糊查询）
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 真实姓名（模糊查询）
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 部门
     */
    @Schema(description = "部门")
    private String department;

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
