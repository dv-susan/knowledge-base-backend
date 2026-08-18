package com.knowledge.base.search.controller;

import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.search.dto.SearchRequestDTO;
import com.knowledge.base.search.service.SearchHistoryService;
import com.knowledge.base.search.service.SearchService;
import com.knowledge.base.search.vo.SearchHistoryVO;
import com.knowledge.base.search.vo.SearchResultVO;
import com.knowledge.base.search.vo.SearchSuggestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 搜索Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Tag(name = "搜索服务", description = "搜索相关接口")
public class SearchController {

    private final SearchService searchService;
    private final SearchHistoryService searchHistoryService;

    /**
     * 全文搜索
     */
    @PostMapping
    @Operation(summary = "全文搜索", description = "执行全文搜索")
    public Result<PageResult<SearchResultVO>> search(@Valid @RequestBody SearchRequestDTO dto,
                                                      HttpServletRequest request) {
        PageResult<SearchResultVO> result = searchService.search(dto);
        searchHistoryService.saveSearchHistoryAsync(
                UserContextUtil.getUserIdFromHeader(request), dto.getKeyword());
        return Result.success(result);
    }

    /**
     * 高级搜索
     */
    @PostMapping("/advanced")
    @Operation(summary = "高级搜索", description = "执行高级搜索")
    public Result<PageResult<SearchResultVO>> advancedSearch(@Valid @RequestBody SearchRequestDTO dto,
                                                              HttpServletRequest request) {
        PageResult<SearchResultVO> result = searchService.advancedSearch(dto);
        searchHistoryService.saveSearchHistoryAsync(
                UserContextUtil.getUserIdFromHeader(request), dto.getKeyword());
        return Result.success(result);
    }

    /**
     * 搜索建议
     */
    @GetMapping("/suggest")
    @Operation(summary = "搜索建议", description = "获取搜索建议")
    public Result<List<SearchSuggestVO>> suggest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer size) {
        List<SearchSuggestVO> suggestions = searchService.suggest(keyword, size);
        return Result.success(suggestions);
    }

    /**
     * 热门搜索
     */
    @GetMapping("/hot")
    @Operation(summary = "热门搜索", description = "获取系统热门搜索关键词")
    public Result<List<String>> hotSearch() {
        List<String> hotSearches = searchHistoryService.getHotSearch();
        return Result.success(hotSearches);
    }

    /**
     * 获取当前用户搜索历史
     */
    @GetMapping("/history")
    @Operation(summary = "搜索历史", description = "获取当前用户搜索历史")
    public Result<List<SearchHistoryVO>> searchHistory(HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        List<SearchHistoryVO> history = searchHistoryService.getSearchHistory(userId);
        return Result.success(history);
    }

    /**
     * 清空当前用户搜索历史
     */
    @DeleteMapping("/history")
    @Operation(summary = "清空搜索历史", description = "清空当前用户搜索历史")
    public Result<Boolean> clearSearchHistory(HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        Boolean success = searchHistoryService.clearSearchHistory(userId);
        return Result.success(success);
    }

    /**
     * 重建索引
     */
    @PostMapping("/index/rebuild")
    @Operation(summary = "重建索引", description = "重建搜索索引")
    public Result<String> rebuildIndex() {
        searchService.rebuildIndex();
        return Result.success("索引重建任务已提交");
    }

    /**
     * 索引文档（供kb-document内部调用）
     *
     * @param docData 文档数据
     * @return 是否成功
     */
    @PostMapping("/index/document")
    @Operation(summary = "索引文档", description = "从文档数据直接索引到ES（kb-document内部调用）")
    public Result<Boolean> indexDocument(@RequestBody Map<String, Object> docData) {
        searchService.indexDocumentData(docData);
        return Result.success(true);
    }

    /**
     * 删除文档索引（供kb-document内部调用）
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @DeleteMapping("/index/document/{documentId}")
    @Operation(summary = "删除文档索引", description = "从ES删除文档索引（kb-document内部调用）")
    public Result<Boolean> deleteDocumentIndex(@PathVariable Long documentId) {
        searchService.deleteDocument(documentId);
        return Result.success(true);
    }

}
