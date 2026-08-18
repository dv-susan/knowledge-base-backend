package com.knowledge.base.ai.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * KAG 图谱构建消息
 *
 * <p>通过 RabbitMQ 异步传递图谱构建任务。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KAGReindexMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 任务ID（UUID） */
    private String taskId;

    /** 任务类型 */
    private KAGBuildType type;

    /** 文档ID列表 */
    private List<Long> documentIds;

    /**
     * KAG 构建类型
     */
    public enum KAGBuildType {
        /** 全量构建所有已发布文档的知识图谱 */
        BUILD_ALL,
        /** 构建指定文档的知识图谱 */
        BUILD_BY_DOC_IDS,
        /** 删除指定文档的知识图谱 */
        DELETE_BY_DOC_IDS
    }
}
