package com.knowledge.base.document.feign;

import com.knowledge.base.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * KAG图谱构建服务Feign客户端
 *
 * <p>调用 kb-ai 服务的 KAG 图谱构建和删除接口。
 * 用于在文档创建/更新/发布/删除时异步通知 kb-ai 更新 Neo4j 知识图谱。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@FeignClient(
        name = "kb-ai",
        path = "/kag/build",
        url = "${kb-ai.url:#{null}}",
        contextId = "kagFeignClient"
)
public interface KAGFeignClient {

    /**
     * 触发单个文档的KAG图谱构建
     *
     * @param docId 文档ID
     * @return 任务ID
     */
    @PostMapping("/{docId}")
    Result<String> buildGraph(@PathVariable("docId") Long docId);

    /**
     * 删除单个文档的知识图谱
     *
     * @param docId 文档ID
     * @return 任务ID
     */
    @DeleteMapping("/{docId}")
    Result<String> deleteGraph(@PathVariable("docId") Long docId);
}
