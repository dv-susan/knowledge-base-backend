package com.knowledge.base.statistics.mapper;

import com.knowledge.base.statistics.model.IdCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户统计预聚合表 Mapper
 *
 * <p>查询 kb_user_statistics 预聚合表，用于活跃用户排行等场景</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface UserStatisticsAggMapper {

    /**
     * 查询指定时间范围内最活跃的用户（按浏览量）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param limit     返回数量
     * @return 活跃用户排行列表
     */
    List<IdCount> selectTopActiveUsers(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("limit") int limit);
}
