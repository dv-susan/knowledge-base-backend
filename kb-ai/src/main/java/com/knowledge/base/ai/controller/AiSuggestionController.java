package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.config.AiSuggestionProperties;
import com.knowledge.base.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI建议控制器
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/suggestions")
@Tag(name = "AI建议", description = "AI建议相关接口")
public class AiSuggestionController {

    private final AiSuggestionProperties suggestionProperties;

    public AiSuggestionController(AiSuggestionProperties suggestionProperties) {
        this.suggestionProperties = suggestionProperties;
    }

    /**
     * 获取AI建议问题列表
     *
     * @return 建议问题列表
     */
    @GetMapping
    @Operation(summary = "获取AI建议", description = "获取AI快捷问题和建议")
    public Result<List<String>> getSuggestions() {
        List<String> suggestions = suggestionProperties.getItems();
        return Result.success(suggestions);
    }
}
