package com.knowledge.base.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类文档数量统计DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDocCountDTO {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文档数量
     */
    private Integer count;
}
