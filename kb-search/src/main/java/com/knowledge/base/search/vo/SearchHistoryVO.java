package com.knowledge.base.search.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 搜索历史VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索历史")
public class SearchHistoryVO {

    @Schema(description = "历史ID")
    private Long id;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "搜索次数")
    private Integer searchCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
