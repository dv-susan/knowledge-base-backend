package com.knowledge.base.ai.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 重建索引消息体
 *
 * <p>通过 RabbitMQ 发送索引重建任务。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReindexMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 任务ID（UUID） */
    private String taskId;

    /** 重建类型 */
    private ReindexType type;

    /** 指定文档ID列表（type=BY_DOC_IDS时使用） */
    private List<Long> documentIds;

    public enum ReindexType {
        ALL,                // 重建所有已发布文档
        BY_DOC_IDS,         // 重建指定文档
        DELETE_BY_DOC_IDS   // 删除指定文档的向量索引
    }
}
