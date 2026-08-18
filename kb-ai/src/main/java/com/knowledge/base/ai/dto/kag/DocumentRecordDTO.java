package com.knowledge.base.ai.dto.kag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档记录（来自 kb-document Feign 接口的返回数据）
 *
 * <p>用于统一 GraphBuildServiceImpl 和 ReindexConsumer 中
 * Feign 分页/详情返回的文档数据解析。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRecordDTO {

    /** 文档ID */
    private Long id;

    /** 文档标题 */
    private String title;

    /** 文档正文 */
    private String content;

    /** 分类ID */
    private Long categoryId;

    /** 作者ID */
    private Long authorId;

    /** 作者名 */
    private String authorName;

    /** 团队ID */
    private Long teamId;

    /** 文档状态 */
    private Integer status;

    /** 摘要 */
    private String summary;

    /** 发布时间 */
    private String publishTime;
}
