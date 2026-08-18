package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 实体合并参数
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityMergeDTO {

    /** 实体名称 */
    private String name;

    /** 实体类型 */
    private String type;

    /** 实体描述 */
    private String description;

    /** 别名列表 */
    private List<String> aliases;
}
