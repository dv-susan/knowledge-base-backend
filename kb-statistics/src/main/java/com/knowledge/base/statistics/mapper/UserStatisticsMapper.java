package com.knowledge.base.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.statistics.entity.UserStatistics;
import com.knowledge.base.statistics.model.DailyCount;
import com.knowledge.base.statistics.model.IdCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户统计Mapper
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface UserStatisticsMapper extends BaseMapper<UserStatistics> {

    /**
     * 统计用户总数
     */
    Long countAll();

    /**
     * 根据状态统计用户数量
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 统计活跃用户数量（指定时间内有操作记录）
     */
    Long countActiveUsers(@Param("since") LocalDateTime since);

    /**
     * 统计指定时间范围内注册的用户数量
     */
    Long countByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    /**
     * 统计每日新增用户数量
     */
    List<DailyCount> countDailyUsers(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * 统计最活跃的用户（按操作次数）
     */
    List<IdCount> selectMostActiveUsers(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       @Param("limit") Integer limit);

    /**
     * 统计用户操作次数
     */
    Long countUserOperations(@Param("userId") Long userId,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate);

    /**
     * 统计用户发布文档数量
     */
    Long countUserDocuments(@Param("userId") Long userId);

    /**
     * 统计用户评论数量
     */
    Long countUserComments(@Param("userId") Long userId);

    /**
     * 统计用户浏览记录数量
     */
    Long countUserViews(@Param("userId") Long userId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
