package com.knowledge.base.userauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 团队成员实体
 *
 * <p>关联kb_team_member表，记录团队与用户的成员关系</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@TableName("kb_team_member")
@Schema(description = "团队成员实体")
public class TeamMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 团队ID
     */
    @Schema(description = "团队ID")
    private Long teamId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 成员角色：leader-负责人, member-普通成员
     */
    @Schema(description = "成员角色：leader-负责人, member-普通成员")
    private String memberRole;

    /**
     * 加入时间
     */
    @Schema(description = "加入时间")
    private LocalDateTime joinTime;

    /**
     * 添加人ID
     */
    @Schema(description = "添加人ID")
    private Long createBy;
}
