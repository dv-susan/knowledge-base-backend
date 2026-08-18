package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.document.dto.AutoSaveDTO;
import com.knowledge.base.document.dto.DocumentDTO;
import com.knowledge.base.document.entity.Document;
import com.knowledge.base.document.vo.DocumentNeighborVO;
import com.knowledge.base.document.vo.DocumentVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文档Service接口
 *
 * <p>按照阿里巴巴Java开发规范设计，提供文档业务逻辑操作</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface DocumentService extends IService<Document> {

    /**
     * 创建文档
     *
     * @param documentDTO 文档信息
     * @return 文档ID
     */
    Long createDocument(DocumentDTO documentDTO);

    /**
     * 更新文档
     *
     * @param documentDTO 文档信息
     * @return 是否成功
     */
    Boolean updateDocument(DocumentDTO documentDTO);

    /**
     * 更新文档摘要（仅更新summary字段，不做全量校验）
     *
     * @param documentId 文档ID
     * @param summary    摘要内容
     * @return 是否成功
     */
    Boolean updateSummary(Long documentId, String summary);

    /**
     * 删除文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean deleteDocument(Long documentId);

    /**
     * 根据ID查询文档
     *
     * @param documentId 文档ID
     * @return 文档信息
     */
    DocumentVO getDocumentById(Long documentId);

    /**
     * 浏览文档（增加浏览次数）
     *
     * @param documentId 文档ID
     * @return 文档信息
     */
    DocumentVO viewDocument(Long documentId);

    /**
     * 分页查询文档列表
     *
     * @param current    当前页
     * @param size       每页大小
     * @param categoryId 分类ID
     * @param keyword    搜索关键词
     * @param status     状态
     * @param sortBy     排序字段
     * @param sortOrder  排序方向
     * @return 文档分页信息
     */
    IPage<DocumentVO> pageDocuments(Long current, Long size, Long categoryId, Long teamId, String keyword, Integer status, String sortBy, String sortOrder, Long authorId);

    /**
     * 查询文档的上一篇和下一篇
     *
     * @param documentId 当前文档ID
     * @return 相邻文档信息（prevId/prevTitle/nextId/nextTitle）
     */
    DocumentNeighborVO getDocumentNeighbors(Long documentId);

    /**
     * 上传文档文件
     *
     * @param file 文件
     * @return 文件路径
     */
    String uploadDocumentFile(MultipartFile file);

    /**
     * 点赞文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean likeDocument(Long documentId);

    /**
     * 取消点赞文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean unlikeDocument(Long documentId);

    /**
     * 收藏文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean favoriteDocument(Long documentId);

    /**
     * 发布文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean publishDocument(Long documentId);

    /**
     * 归档文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean archiveDocument(Long documentId);

    /**
     * 清理知识图谱脏节点（MySQL中已删除但Neo4j中残留的文档图谱节点）
     *
     * @return 清理的节点数量
     */
    int cleanupGraphGhostNodes();

    /**
     * 上传文件并解析创建文档
     *
     * @param file 上传的文件
     * @return 包含 documentId / title / fileUrl / fileSize / contentLength / contentPreview 的 Map
     */
    Map<String, Object> uploadAndCreateDocument(MultipartFile file);

    /**
     * 重建所有已发布文档的知识图谱
     *
     * @return 重建的文档数量
     */
    int rebuildAllGraphs();

    /**
     * 自动保存文档（创建新草稿或更新已有草稿）
     *
     * <p>与 createDocument/updateDocument 的关键区别：
     * <ul>
     *   <li>标题非必填，空标题自动填充"未命名文档"</li>
     *   <li>强制状态为草稿（0），不触发RAG/KAG/ES索引</li>
     *   <li>仅更新非空字段，避免覆盖已有数据</li>
     * </ul></p>
     *
     * @param autoSaveDTO 自动保存数据
     * @return 文档ID（创建时返回新ID，更新时返回已有ID）
     */
    Long autoSaveDocument(AutoSaveDTO autoSaveDTO);

    /**
     * 放弃自动保存草稿：将当前用户所有草稿（status=0）标记为已确认，
     * 后续不再弹出恢复提示。
     */
    void dismissAutoSaveDrafts();
}
