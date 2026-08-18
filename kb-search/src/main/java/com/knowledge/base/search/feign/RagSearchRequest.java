package com.knowledge.base.search.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG混合搜索请求
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagSearchRequest {

    private String query;

    @Builder.Default
    private int topK = 10;

    @Builder.Default
    private boolean enableRerank = true;
}
