package com.knowledge.base.userauth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 团队实体
 *
 * <p>继承自BaseEntity，复用ID、创建时间、更新时间、创建人、更新人、逻辑删除等通用字段</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_team")
@Schema(description = "团队实体")
public class Team extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 团队层级
     */
    @Schema(description = "团队层级")
    private Integer level;

    /**
     * 团队路径
     */
    @Schema(description = "团队路径")
    private String path;

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
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer sort;
}
