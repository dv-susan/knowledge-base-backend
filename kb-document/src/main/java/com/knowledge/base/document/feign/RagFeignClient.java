package com.knowledge.base.document.feign;

import com.knowledge.base.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * RAG索引服务Feign客户端
 *
 * <p>调用 kb-ai 服务的 RAG 重建索引和删除索引接口。
 * 用于在文档创建/更新/发布/删除时异步通知 kb-ai 更新向量索引。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@FeignClient(
        name = "kb-ai",
        path = "/rag/reindex",
        contextId = "ragFeignClient"
)
public interface RagFeignClient {

    /**
     * 触发单个文档的RAG重建索引
     *
     * <p>文档创建或内容更新后调用，kb-ai 会通过RabbitMQ异步处理。</p>
     *
     * @param docId 文档ID
     * @return 任务ID
     */
    @PostMapping("/{docId}")
    Result<String> reindexDocument(@PathVariable("docId") Long docId);

    /**
     * 删除单个文档的RAG向量索引
     *
     * <p>文档删除、下架或归档时调用，kb-ai 会通过RabbitMQ异步处理。</p>
     *
     * @param docId 文档ID
     * @return 任务ID
     */
    @DeleteMapping("/{docId}")
    Result<String> removeFromIndex(@PathVariable("docId") Long docId);
}
