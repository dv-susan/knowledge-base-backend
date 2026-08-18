package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分块节点属性参数
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkPropsDTO {

    /** 分块ID */
    private String chunkId;

    /** 所属文档ID */
    private Long docId;

    /** 分块内容 */
    private String content;

    /** 分块标题/章节 */
    private String heading;

    /** 分块在文档中的序号 */
    private Integer chunkIndex;

    /** 文档总分块数 */
    private Integer totalChunks;
}
