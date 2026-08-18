package com.knowledge.base.search.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.knowledge.base.search.entity.SearchHistory;
import com.knowledge.base.search.mapper.SearchHistoryMapper;
import com.knowledge.base.search.service.SearchHistoryService;
import com.knowledge.base.search.vo.SearchHistoryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 搜索历史Service实现
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    @Resource
    private SearchHistoryMapper searchHistoryMapper;

    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    /** {@inheritDoc} */
    @Override
    public List<SearchHistoryVO> getSearchHistory(Long userId) {
        LambdaQueryWrapper<SearchHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SearchHistory::getUserId, userId);
        queryWrapper.orderByDesc(SearchHistory::getCreatedAt);

        List<SearchHistory> histories = searchHistoryMapper.selectList(queryWrapper);

        // 按关键词分组，取最新的记录
        Map<String, SearchHistory> keywordMap = histories.stream()
            .collect(Collectors.toMap(
                SearchHistory::getKeyword,
                h -> h,
                (h1, h2) -> h1.getCreatedAt().isAfter(h2.getCreatedAt()) ? h1 : h2
            ));

        return keywordMap.values().stream()
            .sorted(Comparator.comparing(SearchHistory::getCreatedAt).reversed())
            .limit(20)
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean clearSearchHistory(Long userId) {
        LambdaQueryWrapper<SearchHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SearchHistory::getUserId, userId);
        return searchHistoryMapper.delete(queryWrapper) >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSearchHistory(Long historyId, Long userId) {
        LambdaQueryWrapper<SearchHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SearchHistory::getId, historyId);
        queryWrapper.eq(SearchHistory::getUserId, userId);
        return searchHistoryMapper.delete(queryWrapper) > 0;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getHotSearch() {
        // 获取最近7天的热门搜索
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        LambdaQueryWrapper<SearchHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(SearchHistory::getCreatedAt, sevenDaysAgo);
        queryWrapper.orderByDesc(SearchHistory::getSearchCount);
        queryWrapper.last("LIMIT 10");

        return searchHistoryMapper.selectList(queryWrapper).stream()
            .map(SearchHistory::getKeyword)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSearchHistory(Long userId, String keyword) {
        // 查找是否存在相同关键词的记录
        LambdaQueryWrapper<SearchHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SearchHistory::getUserId, userId);
        queryWrapper.eq(SearchHistory::getKeyword, keyword);

        SearchHistory existingHistory = searchHistoryMapper.selectOne(queryWrapper);

        if (existingHistory != null) {
            // 更新搜索次数和时间
            existingHistory.setSearchCount(existingHistory.getSearchCount() + 1);
            existingHistory.setCreatedAt(LocalDateTime.now());
            searchHistoryMapper.updateById(existingHistory);
        } else {
            // 创建新的搜索历史记录
            SearchHistory newHistory = new SearchHistory();
            newHistory.preInsert(); // 雪花算法生成ID
            newHistory.setUserId(userId);
            newHistory.setKeyword(keyword);
            newHistory.setSearchCount(1);
            newHistory.setCreatedAt(LocalDateTime.now());
            searchHistoryMapper.insert(newHistory);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveSearchHistoryAsync(Long userId, String keyword) {
        CompletableFuture.runAsync(() -> {
            try {
                saveSearchHistory(userId, keyword);
            } catch (Exception e) {
                log.warn("保存搜索历史失败：keyword={}, error={}", keyword, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 转换为VO
     *
     * @param history 搜索历史实体
     * @return 搜索历史VO
     */
    private SearchHistoryVO convertToVO(SearchHistory history) {
        SearchHistoryVO vo = new SearchHistoryVO();
        BeanUtils.copyProperties(history, vo);
        return vo;
    }
}
