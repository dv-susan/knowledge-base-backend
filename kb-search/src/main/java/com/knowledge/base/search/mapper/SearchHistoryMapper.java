package com.knowledge.base.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.search.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 搜索历史Mapper
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
}
