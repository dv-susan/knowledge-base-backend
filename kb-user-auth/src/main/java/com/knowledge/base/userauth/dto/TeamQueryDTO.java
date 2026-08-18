package com.knowledge.base.userauth.dto;

import com.knowledge.base.common.result.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 团队查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "团队查询请求")
public class TeamQueryDTO extends PageParam {

    private static final long serialVersionUID = 1L;

    /**
     * 团队名称（模糊查询）
     */
    @Schema(description = "团队名称")
    private String teamName;

    /**
     * 团队编码
     */
    @Schema(description = "团队编码")
    private String teamCode;

    /**
     * 父团队ID
     */
    @Schema(description = "父团队ID")
    private Long parentId;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
