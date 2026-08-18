package com.knowledge.base.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.ai.service.AiConversationService;
import com.knowledge.base.ai.vo.ConversationVO;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI对话管理控制器
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
@Tag(name = "AI对话管理", description = "AI对话管理相关接口")
public class AiConversationController {

    private final AiConversationService conversationService;

    /**
     * 创建新对话
     *
     * @param body    请求体（包含title）
     * @param request HTTP请求
     * @return 新创建的对话
     */
    @PostMapping
    @Operation(summary = "创建新对话", description = "创建新的AI对话")
    public Result<ConversationVO> createConversation(@RequestBody Map<String, String> body,
                                                       HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        String title = body.getOrDefault("title", "新对话");
        Long conversationId = conversationService.createConversation(title, userId);
        ConversationVO conversation = conversationService.getConversation(conversationId, userId);
        return Result.success(conversation);
    }

    /**
     * 获取对话列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param request HTTP请求
     * @return 对话列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取对话列表", description = "获取当前用户的对话列表")
    public Result<IPage<ConversationVO>> listConversations(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        IPage<ConversationVO> conversations = conversationService.listConversations(userId, current, size);
        return Result.success(conversations);
    }

    /**
     * 获取对话详情
     *
     * @param id      对话ID
     * @param request HTTP请求
     * @return 对话详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取对话详情", description = "获取对话详细信息")
    public Result<ConversationVO> getConversation(@PathVariable Long id,
                                                   HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        ConversationVO conversation = conversationService.getConversation(id, userId);
        return Result.success(conversation);
    }

    /**
     * 删除对话
     *
     * @param id      对话ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除对话", description = "删除指定对话")
    public Result<Boolean> deleteConversation(@PathVariable Long id,
                                               HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        boolean success = conversationService.deleteConversation(id, userId);
        return Result.success(success);
    }
}
