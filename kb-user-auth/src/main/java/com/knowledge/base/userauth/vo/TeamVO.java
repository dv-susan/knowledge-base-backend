package com.knowledge.base.userauth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 团队VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "团队信息")
public class TeamVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 团队ID
     */
    @Schema(description = "团队ID")
    private Long id;

    /**
     * 团队名称
     */
    @Schema(description = "团队名称")
    private String teamName;

    /**
     * 团队编码
     */
    @Schema(description = "团队编码")
    private String teamCode;

    /**
     * 团队描述
     */
    @Schema(description = "团队描述")
    private String description;

    /**
     * 团队图标
     */
    @Schema(description = "团队图标")
    private String icon;

    /**
     * 父团队ID
     */
    @Schema(description = "父团队ID")
    private Long parentId;

    /**
     * 父团队名称
     */
    @Schema(description = "父团队名称")
    private String parentName;

    /**
     * 团队层级
     */
    @Schema(description = "团队层级")
    private Integer level;

    /**
     * 成员数量
     */
    @Schema(description = "成员数量")
    private Integer memberCount;

    /**
     * 文档数量
     */
    @Schema(description = "文档数量")
    private Integer docCount;

    /**
     * 团队负责人ID
     */
    @Schema(description = "团队负责人ID")
    private Long leaderId;

    /**
     * 团队负责人名称
     */
    @Schema(description = "团队负责人名称")
    private String leaderName;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 子团队列表
     */
    @Schema(description = "子团队列表")
    private List<TeamVO> children;
}
