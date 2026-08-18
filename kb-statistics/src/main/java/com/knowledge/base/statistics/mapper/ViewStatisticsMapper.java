package com.knowledge.base.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.statistics.entity.ViewStatistics;
import com.knowledge.base.statistics.model.DailyCount;
import com.knowledge.base.statistics.model.IdCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 浏览统计Mapper
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface ViewStatisticsMapper extends BaseMapper<ViewStatistics> {

    /**
     * 统计浏览记录总数
     */
    Long countAll();

    /**
     * 根据用户ID统计浏览数量
     */
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 根据文档ID统计浏览数量
     */
    Long countByDocumentId(@Param("documentId") Long documentId);

    /**
     * 统计指定时间范围内的浏览数量
     */
    Long countByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    /**
     * 统计用户在指定时间范围内的浏览数量
     */
    Long countByUserIdAndDateRange(@Param("userId") Long userId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * 统计每日浏览数量
     */
    List<DailyCount> countDailyViews(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * 统计文档在指定时间范围内的浏览数量
     */
    Long countByDocumentIdAndDateRange(@Param("documentId") Long documentId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 统计最热门的文档（按浏览量）
     */
    List<IdCount> selectMostViewedDocuments(@Param("limit") Integer limit);

    /**
     * 统计最活跃的浏览用户
     */
    List<IdCount> selectMostActiveViewers(@Param("limit") Integer limit);

    /**
     * 查询用户浏览过的文档ID列表
     */
    List<Long> selectViewedDocumentIdsByUserId(@Param("userId") Long userId,
                                                @Param("limit") Integer limit);

    /**
     * 检查用户是否浏览过指定文档
     */
    Boolean hasViewedDocument(@Param("userId") Long userId,
                              @Param("documentId") Long documentId);

    /**
     * 查询用户最近浏览记录
     */
    List<ViewStatistics> selectRecentViewsByUserId(@Param("userId") Long userId,
                                                    @Param("limit") Integer limit);

    /**
     * 统计活跃用户数（近30天有浏览记录的不重复用户数）
     */
    Long countActiveUsers(@Param("sinceDate") LocalDateTime sinceDate);
}
