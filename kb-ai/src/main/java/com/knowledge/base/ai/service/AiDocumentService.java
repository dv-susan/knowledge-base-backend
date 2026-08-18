package com.knowledge.base.ai.service;

import com.knowledge.base.ai.dto.DocumentProcessDTO;
import com.knowledge.base.ai.vo.DocumentProcessVO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI文档处理服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface AiDocumentService {

    /**
     * 生成文档摘要
     *
     * @param content 文档内容
     * @param length  摘要长度
     * @return 摘要内容
     */
    String generateSummary(String content, Integer length);

    /**
     * 生成文档大纲
     *
     * @param content 文档内容
     * @param level   大纲层级
     * @return 大纲内容
     */
    String generateOutline(String content, Integer level);

    /**
     * 扩展内容
     *
     * @param content  文档内容
     * @param expType  扩展类型
     * @return 扩展内容
     */
    String expandContent(String content, String expType);

    /**
     * 优化表达
     *
     * @param content  文档内容
     * @param target   优化目标
     * @return 优化内容
     */
    String optimizeContent(String content, String target);

    /**
     * 添加示例
     *
     * @param content  文档内容
     * @param expType  示例类型
     * @return 添加示例后的内容
     */
    String addExample(String content, String expType);

    /**
     * 处理文档（统一入口）
     *
     * @param processDTO 处理请求
     * @return 处理结果
     */
    DocumentProcessVO processDocument(DocumentProcessDTO processDTO);

    /**
     * 生成文档摘要（文件上传方式）
     *
     * @param file   文档文件
     * @param userId 用户ID
     * @return 处理结果
     */
    DocumentProcessVO generateSummary(MultipartFile file, Long userId);

    /**
     * 生成文档大纲（文件上传方式）
     *
     * @param file   文档文件
     * @param userId 用户ID
     * @return 处理结果
     */
    DocumentProcessVO generateOutline(MultipartFile file, Long userId);

    /**
     * 扩展内容（DTO方式）
     *
     * @param dto    处理请求
     * @param userId 用户ID
     * @return 处理结果
     */
    DocumentProcessVO expandContent(DocumentProcessDTO dto, Long userId);

    /**
     * 优化内容（DTO方式）
     *
     * @param dto    处理请求
     * @param userId 用户ID
     * @return 处理结果
     */
    DocumentProcessVO optimizeContent(DocumentProcessDTO dto, Long userId);

    /**
     * 流式生成摘要
     *
     * @param file   文档文件
     * @param userId 用户ID
     * @return SSE事件流
     */
    SseEmitter generateSummaryStream(MultipartFile file, Long userId);

    /**
     * 流式生成大纲
     *
     * @param file   文档文件
     * @param userId 用户ID
     * @return SSE事件流
     */
    SseEmitter generateOutlineStream(MultipartFile file, Long userId);

    /**
     * 基于内容生成摘要（非流式）
     *
     * @param dto    处理请求
     * @param userId 用户ID
     * @return 处理结果
     */
    DocumentProcessVO generateSummaryByContent(DocumentProcessDTO dto, Long userId);

    /**
     * 基于内容流式生成摘要
     *
     * @param dto    处理请求
     * @param userId 用户ID
     * @return SSE事件流
     */
    SseEmitter generateSummaryByContentStream(DocumentProcessDTO dto, Long userId);
}