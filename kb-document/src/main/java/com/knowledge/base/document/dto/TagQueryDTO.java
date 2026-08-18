package com.knowledge.base.document.dto;

import com.knowledge.base.common.result.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "标签查询请求")
public class TagQueryDTO extends PageParam {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称（模糊查询）
     */
    @Schema(description = "标签名称")
    private String tagName;

    /**
     * 标签类型：0-SYSTEM，1-USER
     */
    @Schema(description = "标签类型")
    private Integer tagType;

    /**
     * 所属分类ID
     */
    @Schema(description = "所属分类ID")
    private Long categoryId;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
