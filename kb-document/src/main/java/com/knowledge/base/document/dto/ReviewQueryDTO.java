package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 审核查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "审核查询请求")
public class ReviewQueryDTO {

    @Schema(description = "当前页")
    private Long current = 1L;

    @Schema(description = "每页大小")
    private Long size = 10L;

    @Schema(description = "审核状态：0-待审核，1-通过，2-驳回")
    private Integer status;

    @Schema(description = "审核人ID")
    private Long reviewerId;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "关键词搜索")
    private String keyword;
}
