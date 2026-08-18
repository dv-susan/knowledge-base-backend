package com.knowledge.base.statistics.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * AI统计数据 Mapper
 *
 * <p>通过跨库视图 kb_ai_conversation / kb_ai_message 查询 AI 使用统计</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface AiStatisticsMapper {

    /**
     * 统计 AI 对话总数（智能搜索次数）
     */
    Long countConversations();

    /**
     * 统计 AI 用户提问总数（问答次数）
     */
    Long countUserMessages();
}
