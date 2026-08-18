package com.knowledge.base.search.feign;

import com.knowledge.base.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * kb-ai RAG混合搜索Feign客户端
 *
 * @author 苏三
 * @since 1.0.0
 */
@FeignClient(
        name = "kb-ai",
        path = "/rag",
        contextId = "ragSearchFeignClient"
)
public interface RagSearchFeignClient {

    /**
     * 混合搜索 (BM25 + kNN + RRF融合)
     *
     * @param request 搜索请求
     * @return 搜索结果列表
     */
    @PostMapping("/search")
    Result<List<RagSearchItemVO>> search(@RequestBody RagSearchRequest request);
}
