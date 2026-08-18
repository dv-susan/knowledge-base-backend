package com.knowledge.base.document.feign;

import com.knowledge.base.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱服务Feign客户端
 *
 * <p>调用 kb-graph 服务的图谱删除接口，直接在 Neo4j 中删除文档图谱数据，
 * 作为 KAG RabbitMQ 异步删除的同步备份路径。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@FeignClient(
        name = "kb-graph",
        path = "/graph",
        contextId = "graphFeignClient"
)
public interface GraphFeignClient {

    /**
     * 删除指定文档的知识图谱数据
     *
     * @param docId 文档ID
     * @return 操作结果
     */
    @DeleteMapping("/document/{docId}")
    Result<String> deleteDocumentGraph(@PathVariable("docId") Long docId);

    /**
     * 清理脏图谱节点（docId 不在白名单中的节点将被删除）
     *
     * @param body 包含 validDocIds 列表的请求体
     * @return 操作结果
     */
    @PostMapping("/document/cleanup")
    Result<String> cleanupDocumentGraph(@RequestBody Map<String, List<Long>> body);
}
