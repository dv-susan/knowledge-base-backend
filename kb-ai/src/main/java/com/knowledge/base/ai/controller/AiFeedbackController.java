package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.dto.FeedbackDTO;
import com.knowledge.base.ai.service.AiFeedbackService;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI反馈控制器
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "AI反馈", description = "AI反馈相关接口")
public class AiFeedbackController {

    private final AiFeedbackService feedbackService;

    /**
     * 提交反馈
     *
     * @param feedbackDTO 反馈信息
     * @param request     HTTP请求
     * @return 是否成功
     */
    @PostMapping
    @Operation(summary = "提交反馈", description = "提交AI使用反馈")
    public Result<Boolean> submitFeedback(
            @Valid @RequestBody FeedbackDTO feedbackDTO,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        Boolean success = feedbackService.submitFeedback(feedbackDTO, userId);
        return Result.success(success);
    }

    /**
     * 获取用户反馈列表
     *
     * @param request HTTP请求
     * @return 反馈列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取反馈列表", description = "获取当前用户的反馈列表")
    public Result<?> getUserFeedbacks(HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        return Result.success(feedbackService.getUserFeedbacks(userId));
    }

}
