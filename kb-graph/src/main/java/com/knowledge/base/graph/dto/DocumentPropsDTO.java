package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档节点属性参数
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentPropsDTO {

    /** 文档ID */
    private Long docId;

    /** 文档标题 */
    private String title;

    /** 文档摘要 */
    private String summary;

    /** 分类ID */
    private Long categoryId;

    /** 作者ID */
    private Long authorId;

    /** 作者名 */
    private String authorName;

    /** 文档状态 */
    private Integer status;
}
