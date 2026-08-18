package com.knowledge.base.statistics.service;

import com.knowledge.base.statistics.vo.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计分析Service接口
 *
 * <p>提供数据统计相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface StatisticsService {

    /**
     * 获取数据概览
     *
     * @return 数据概览信息
     */
    OverviewVO getOverview();

    /**
     * 获取文档趋势
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param type      趋势类型（create、view、like、favorite）
     * @return 趋势数据列表
     */
    List<TrendVO> getDocumentTrend(LocalDate startDate, LocalDate endDate, String type);

    /**
     * 获取用户活跃度
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 活跃度数据列表
     */
    List<UserActivityVO> getUserActivity(LocalDate startDate, LocalDate endDate);

    /**
     * 获取分类分布
     *
     * @return 分类分布数据列表
     */
    List<CategoryDistributionVO> getCategoryDistribution();

    /**
     * 获取热门文档
     *
     * @param type 统计类型（view、like、favorite）
     * @param size 数量
     * @return 热门文档列表
     */
    List<HotDocumentVO> getHotDocuments(String type, Integer size);

    /**
     * 获取活跃用户
     *
     * @param type 统计类型（create、comment、view）
     * @param size 数量
     * @return 活跃用户列表
     */
    List<ActiveUserVO> getActiveUsers(String type, Integer size);

    /**
     * 获取仪表盘数据
     *
     * @return 仪表盘综合数据
     */
    DashboardVO getDashboardData();

    /**
     * 获取最新文档
     *
     * @param size 数量
     * @return 最新文档列表
     */
    List<HotDocumentVO> getLatestDocuments(Integer size);

    /**
     * 获取管理后台概览
     *
     * @return 管理后台概览数据
     */
    AdminOverviewVO getAdminOverview();
}
