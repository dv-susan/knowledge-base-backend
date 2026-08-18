package com.knowledge.base.search.service;

import com.knowledge.base.search.vo.SearchHistoryVO;

import java.util.List;

/**
 * 搜索历史Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface SearchHistoryService {

    /**
     * 获取搜索历史
     *
     * @param userId 用户ID
     * @return 搜索历史列表
     */
    List<SearchHistoryVO> getSearchHistory(Long userId);

    /**
     * 清空搜索历史
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean clearSearchHistory(Long userId);

    /**
     * 删除搜索历史
     *
     * @param historyId 历史ID
     * @param userId    用户ID
     * @return 是否成功
     */
    Boolean deleteSearchHistory(Long historyId, Long userId);

    /**
     * 获取热门搜索
     *
     * @return 热门搜索关键词列表
     */
    List<String> getHotSearch();

    /**
     * 保存搜索历史
     *
     * @param userId  用户ID
     * @param keyword 搜索关键词
     */
    void saveSearchHistory(Long userId, String keyword);

    /**
     * 异步保存搜索历史（不阻塞主流程）
     *
     * @param userId  用户ID
     * @param keyword 搜索关键词
     */
    void saveSearchHistoryAsync(Long userId, String keyword);
}
