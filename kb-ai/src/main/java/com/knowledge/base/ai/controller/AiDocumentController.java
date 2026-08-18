package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.dto.DocumentProcessDTO;
import com.knowledge.base.ai.service.AiDocumentService;
import com.knowledge.base.ai.vo.DocumentProcessVO;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI文档处理控制器
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
@Tag(name = "AI文档处理", description = "AI文档处理相关接口")
public class AiDocumentController {

    private final AiDocumentService aiDocumentService;

    /**
     * 生成文档摘要
     *
     * @param file    文档文件
     * @param request HTTP请求
     * @return 摘要结果
     */
    @PostMapping("/summary")
    @Operation(summary = "生成文档摘要", description = "使用AI生成文档摘要")
    public Result<DocumentProcessVO> generateSummary(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        DocumentProcessVO result = aiDocumentService.generateSummary(file, userId);
        return Result.success(result);
    }

    /**
     * 生成文档大纲
     *
     * @param file    文档文件
     * @param request HTTP请求
     * @return 大纲结果
     */
    @PostMapping("/outline")
    @Operation(summary = "生成文档大纲", description = "使用AI生成文档大纲")
    public Result<DocumentProcessVO> generateOutline(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        DocumentProcessVO result = aiDocumentService.generateOutline(file, userId);
        return Result.success(result);
    }

    /**
     * 内容扩展
     *
     * @param dto     处理请求
     * @param request HTTP请求
     * @return 扩展结果
     */
    @PostMapping("/expand")
    @Operation(summary = "内容扩展", description = "使用AI扩展文档内容")
    public Result<DocumentProcessVO> expandContent(
            @Valid @RequestBody DocumentProcessDTO dto,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        DocumentProcessVO result = aiDocumentService.expandContent(dto, userId);
        return Result.success(result);
    }

    /**
     * 表达优化
     *
     * @param dto     处理请求
     * @param request HTTP请求
     * @return 优化结果
     */
    @PostMapping("/optimize")
    @Operation(summary = "表达优化", description = "使用AI优化文档表达")
    public Result<DocumentProcessVO> optimizeContent(
            @Valid @RequestBody DocumentProcessDTO dto,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        DocumentProcessVO result = aiDocumentService.optimizeContent(dto, userId);
        return Result.success(result);
    }

    /**
     * 流式生成摘要
     *
     * @param file    文档文件
     * @param request HTTP请求
     * @return SSE事件流
     */
    @PostMapping("/summary/stream")
    @Operation(summary = "流式生成摘要", description = "使用AI流式生成文档摘要")
    public SseEmitter generateSummaryStream(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        return aiDocumentService.generateSummaryStream(file, userId);
    }

    /**
     * 流式生成大纲
     *
     * @param file    文档文件
     * @param request HTTP请求
     * @return SSE事件流
     */
    @PostMapping("/outline/stream")
    @Operation(summary = "流式生成大纲", description = "使用AI流式生成文档大纲")
    public SseEmitter generateOutlineStream(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        return aiDocumentService.generateOutlineStream(file, userId);
    }

    /**
     * 基于内容生成摘要（非流式）
     *
     * @param dto     包含文档内容的处理请求
     * @param request HTTP请求
     * @return 摘要结果
     */
    @PostMapping("/summary/content")
    @Operation(summary = "基于内容生成摘要", description = "传入文档内容，使用AI生成摘要")
    public Result<DocumentProcessVO> generateSummaryByContent(
            @Valid @RequestBody DocumentProcessDTO dto,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        DocumentProcessVO result = aiDocumentService.generateSummaryByContent(dto, userId);
        return Result.success(result);
    }

    /**
     * 基于内容流式生成摘要
     *
     * @param dto     包含文档内容的处理请求
     * @param request HTTP请求
     * @return SSE事件流
     */
    @PostMapping("/summary/content/stream")
    @Operation(summary = "基于内容流式生成摘要", description = "传入文档内容，使用AI流式生成摘要")
    public SseEmitter generateSummaryByContentStream(
            @Valid @RequestBody DocumentProcessDTO dto,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        return aiDocumentService.generateSummaryByContentStream(dto, userId);
    }

}
