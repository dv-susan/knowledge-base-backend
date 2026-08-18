package com.knowledge.base.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.statistics.entity.DocumentStatistics;
import com.knowledge.base.statistics.model.DailyCount;
import com.knowledge.base.statistics.model.IdCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档统计Mapper
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface DocumentStatisticsMapper extends BaseMapper<DocumentStatistics> {

    /**
     * 统计文档总数
     */
    Long countAll();

    /**
     * 根据状态统计文档数量
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 根据作者ID统计文档数量
     */
    Long countByAuthorId(@Param("authorId") Long authorId);

    /**
     * 根据分类ID统计文档数量
     */
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 统计指定时间范围内的文档数量
     */
    Long countByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    /**
     * 统计每日文档创建数量
     */
    List<DailyCount> countDailyDocuments(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * 统计最热门的文档（按浏览量）
     */
    List<DocumentStatistics> selectMostViewedDocuments(@Param("limit") Integer limit);

    /**
     * 统计最受欢迎的文档（按点赞数）
     */
    List<DocumentStatistics> selectMostLikedDocuments(@Param("limit") Integer limit);

    /**
     * 统计最多收藏的文档
     */
    List<DocumentStatistics> selectMostFavoritedDocuments(@Param("limit") Integer limit);

    /**
     * 统计最活跃的作者
     */
    List<IdCount> selectTopAuthors(@Param("limit") Integer limit);

    /**
     * 统计各分类的文档数量
     */
    List<IdCount> countByCategory();

    /**
     * 统计总浏览量
     */
    Long sumViewCount();

    /**
     * 统计总点赞数
     */
    Long sumLikeCount();

    /**
     * 统计总收藏数
     */
    Long sumFavoriteCount();
}
