package com.knowledge.base.ai.dto.kag.extraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 抽取的知识实体
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 实体名称 */
    private String name;

    /** 实体类型（TECH_STACK / API / CONFIG / CONCEPT / TOOL / PROCESS） */
    private String type;

    /** 实体描述 */
    private String description;

    /** 别名列表 */
    private List<String> aliases;

    /** 抽取置信度 */
    @Builder.Default
    private Double confidence = 0.8;
}
