package com.knowledge.base.ai.service;

import com.knowledge.base.ai.dto.WritingRequestDTO;
import com.knowledge.base.ai.vo.WritingResultVO;
import com.knowledge.base.ai.vo.WritingTemplateVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * AI写作服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface AiWritingService {

    /**
     * 根据主题和需求生成写作内容
     *
     * @param dto    写作请求参数
     * @param userId 用户ID
     * @return 写作结果
     */
    WritingResultVO generate(WritingRequestDTO dto, Long userId);

    /**
     * 流式生成写作内容（SSE）
     *
     * @param dto    写作请求参数
     * @param userId 用户ID
     * @return SSE事件发射器
     */
    SseEmitter generateStream(WritingRequestDTO dto, Long userId);

    /**
     * 扩写已有内容
     *
     * @param dto    写作请求参数（需包含existingContent）
     * @param userId 用户ID
     * @return 写作结果
     */
    WritingResultVO expand(WritingRequestDTO dto, Long userId);

    /**
     * 优化/润色已有内容
     *
     * @param dto    写作请求参数（需包含existingContent）
     * @param userId 用户ID
     * @return 写作结果
     */
    WritingResultVO optimize(WritingRequestDTO dto, Long userId);

    /**
     * 从已有内容续写
     *
     * @param dto    写作请求参数（需包含existingContent）
     * @param userId 用户ID
     * @return 写作结果
     */
    WritingResultVO continueWriting(WritingRequestDTO dto, Long userId);

    /**
     * 获取写作提示模板列表
     *
     * @return 模板列表
     */
    List<WritingTemplateVO> getTemplates();
}
