package com.knowledge.base.search.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.search.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索历史Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/history")
@Tag(name = "搜索历史", description = "搜索历史管理接口")
public class SearchHistoryController {

    @Resource
    private SearchHistoryService searchHistoryService;

    /**
     * 删除指定搜索历史
     *
     * @param historyId 历史ID
     * @param request   HTTP请求
     * @return 是否成功
     */
    @DeleteMapping("/{historyId}")
    @Operation(summary = "删除搜索历史", description = "删除指定的搜索历史记录")
    public Result<Boolean> deleteSearchHistory(
            @PathVariable Long historyId,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        Boolean success = searchHistoryService.deleteSearchHistory(historyId, userId);
        return Result.success(success);
    }

    /**
     * 获取热门搜索
     *
     * @return 热门搜索列表
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门搜索", description = "获取系统热门搜索关键词")
    public Result<List<String>> getHotSearch() {
        List<String> hotSearch = searchHistoryService.getHotSearch();
        return Result.success(hotSearch);
    }

}
