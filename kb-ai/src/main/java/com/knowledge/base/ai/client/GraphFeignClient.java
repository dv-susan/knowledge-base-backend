package com.knowledge.base.ai.client;

import com.knowledge.base.ai.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * kb-graph 服务 Feign 客户端
 *
 * <p>用于 kb-ai 在知识图谱重建完成后清除 kb-graph 的 Redis 缓存，
 * 确保前端查询知识图谱时获取最新数据。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@FeignClient(name = "kb-graph", url = "${kb-graph.url:#{null}}",
        path = "/graph",
        configuration = InternalFeignConfig.class)
public interface GraphFeignClient {

    /**
     * 清除图谱所有 Redis 缓存
     *
     * <p>知识图谱重建后调用，使 kb-graph 的 @Cacheable 缓存全部失效，
     * 下次查询时强制从 Neo4j 重新加载。</p>
     *
     * @return 操作结果
     */
    @PostMapping("/cache/evict")
    Map<String, Object> evictAllGraphCaches();
}
