package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.dto.WritingRequestDTO;
import com.knowledge.base.ai.service.AiWritingService;
import com.knowledge.base.ai.vo.WritingResultVO;
import com.knowledge.base.ai.vo.WritingTemplateVO;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * AI写作控制器
 *
 * <p>提供AI写作相关接口，包括内容生成、扩写、优化、续写和模板管理。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/writing")
@RequiredArgsConstructor
@Tag(name = "AI写作", description = "AI写作相关接口")
public class AiWritingController {

    private final AiWritingService aiWritingService;

    /**
     * 根据主题和需求生成写作内容
     *
     * @param dto     写作请求参数
     * @param request HTTP请求
     * @return 写作结果
     */
    @PostMapping("/generate")
    @Operation(summary = "生成写作内容", description = "根据写作主题和需求参数，使用AI生成写作内容")
    public Result<WritingResultVO> generate(
            @Valid @RequestBody WritingRequestDTO dto,
            HttpServletRequest request) {
        log.info("AI写作生成请求：topic={}, contentType={}, style={}", dto.getTopic(), dto.getContentType(), dto.getStyle());
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        WritingResultVO result = aiWritingService.generate(dto, userId);
        return Result.success(result);
    }

    /**
     * 流式生成写作内容（SSE）
     *
     * @param dto     写作请求参数
     * @param request HTTP请求
     * @return SSE事件流
     */
    @PostMapping("/generate/stream")
    @Operation(summary = "流式生成写作内容", description = "流式（SSE）生成写作内容，实时返回生成结果")
    public SseEmitter generateStream(
            @Valid @RequestBody WritingRequestDTO dto,
            HttpServletRequest request) {
        log.info("AI写作流式生成请求：topic={}", dto.getTopic());
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        return aiWritingService.generateStream(dto, userId);
    }

    /**
     * 扩写已有内容
     *
     * @param dto     写作请求参数（需包含existingContent）
     * @param request HTTP请求
     * @return 写作结果
     */
    @PostMapping("/expand")
    @Operation(summary = "扩写内容", description = "对已有内容进行扩展，丰富细节和深度")
    public Result<WritingResultVO> expand(
            @Valid @RequestBody WritingRequestDTO dto,
            HttpServletRequest request) {
        log.info("AI写作扩写请求：topic={}, existingContentLength={}",
                dto.getTopic(), dto.getExistingContent() != null ? dto.getExistingContent().length() : 0);
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        WritingResultVO result = aiWritingService.expand(dto, userId);
        return Result.success(result);
    }

    /**
     * 优化/润色内容
     *
     * @param dto     写作请求参数（需包含existingContent）
     * @param request HTTP请求
     * @return 写作结果
     */
    @PostMapping("/optimize")
    @Operation(summary = "优化润色", description = "对已有内容进行优化和润色，提升表达质量")
    public Result<WritingResultVO> optimize(
            @Valid @RequestBody WritingRequestDTO dto,
            HttpServletRequest request) {
        log.info("AI写作优化请求：topic={}, existingContentLength={}",
                dto.getTopic(), dto.getExistingContent() != null ? dto.getExistingContent().length() : 0);
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        WritingResultVO result = aiWritingService.optimize(dto, userId);
        return Result.success(result);
    }

    /**
     * 续写内容
     *
     * @param dto     写作请求参数（需包含existingContent）
     * @param request HTTP请求
     * @return 写作结果
     */
    @PostMapping("/continue")
    @Operation(summary = "续写内容", description = "从已有内容的结尾处继续写作")
    public Result<WritingResultVO> continueWriting(
            @Valid @RequestBody WritingRequestDTO dto,
            HttpServletRequest request) {
        log.info("AI写作续写请求：topic={}, existingContentLength={}",
                dto.getTopic(), dto.getExistingContent() != null ? dto.getExistingContent().length() : 0);
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        WritingResultVO result = aiWritingService.continueWriting(dto, userId);
        return Result.success(result);
    }

    /**
     * 获取写作提示模板
     *
     * @return 模板列表
     */
    @GetMapping("/templates")
    @Operation(summary = "获取写作模板", description = "获取预设的写作提示模板列表，用于快速开始写作")
    public Result<List<WritingTemplateVO>> getTemplates() {
        log.info("获取写作模板列表");
        List<WritingTemplateVO> templates = aiWritingService.getTemplates();
        return Result.success(templates);
    }
}
