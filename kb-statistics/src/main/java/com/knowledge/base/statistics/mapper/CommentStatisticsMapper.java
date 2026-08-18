package com.knowledge.base.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.statistics.entity.CommentStatistics;
import com.knowledge.base.statistics.model.DailyCount;
import com.knowledge.base.statistics.model.IdCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论统计Mapper
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface CommentStatisticsMapper extends BaseMapper<CommentStatistics> {

    /**
     * 根据用户ID统计评论数量
     */
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 根据文档ID统计评论数量
     */
    Long countByDocumentId(@Param("documentId") Long documentId);

    /**
     * 统计指定时间范围内的评论数量
     */
    Long countByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    /**
     * 统计每日评论数量
     */
    List<DailyCount> countDailyComments(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 统计用户在指定时间范围内的评论数量
     */
    Long countByUserIdAndDateRange(@Param("userId") Long userId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * 统计最活跃的评论用户 (user_id, comment_count)
     */
    List<IdCount> selectTopCommenters(@Param("limit") Integer limit);

    /**
     * 统计评论最多的文档 (document_id, comment_count)
     */
    List<IdCount> selectMostCommentedDocuments(@Param("limit") Integer limit);

    /**
     * 统计平均每日评论数量
     */
    Double getAverageDailyComments(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
}
