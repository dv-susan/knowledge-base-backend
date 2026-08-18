package com.knowledge.base.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 重建索引请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "重建索引请求")
public class ReindexRequestDTO {

    @Schema(description = "文档ID列表")
    private List<Long> documentIds;
}
