package com.knowledge.base.document.feign;

import com.knowledge.base.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 搜索索引Feign客户端
 *
 * <p>调用 kb-search 服务的文档索引接口，在文档创建/更新/发布/删除时
 * 同步文档元数据到 ES kb_document 索引，用于关键词搜索。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@FeignClient(
        name = "kb-search",
        path = "/index",
        contextId = "searchIndexFeignClient"
)
public interface SearchIndexFeignClient {

    /**
     * 索引文档
     *
     * @param docData 文档数据（id, title, summary, content, categoryId,
     *                categoryName, tags, authorId, authorName, status,
     *                isPublic, viewCount, likeCount, commentCount,
     *                publishTime, createTime, updateTime）
     * @return 是否成功
     */
    @PostMapping("/document")
    Result<Boolean> indexDocument(@RequestBody Map<String, Object> docData);

    /**
     * 删除文档索引
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @DeleteMapping("/document/{documentId}")
    Result<Boolean> deleteDocumentIndex(@PathVariable("documentId") Long documentId);
}
