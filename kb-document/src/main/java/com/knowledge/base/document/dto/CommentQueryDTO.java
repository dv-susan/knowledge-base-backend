package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 评论查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "评论查询请求")
public class CommentQueryDTO {

    @Schema(description = "当前页")
    private Long current = 1L;

    @Schema(description = "每页大小")
    private Long size = 10L;

    @Schema(description = "排序方式：like_count-点赞数，created_at-创建时间")
    private String sortBy = "created_at";

    @Schema(description = "排序方向：asc-升序，desc-降序")
    private String sortOrder = "desc";
}
