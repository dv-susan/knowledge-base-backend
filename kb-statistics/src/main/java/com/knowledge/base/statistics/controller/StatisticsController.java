package com.knowledge.base.statistics.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.statistics.service.StatisticsService;
import com.knowledge.base.statistics.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计分析Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供数据统计分析相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("")
@Tag(name = "统计分析", description = "数据统计分析接口")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 获取数据概览
     *
     * @return 数据概览信息
     */
    @GetMapping("/overview")
    @Operation(summary = "数据概览", description = "获取系统数据概览信息")
    public Result<OverviewVO> getOverview() {
        log.info("获取数据概览请求");

        OverviewVO overview = statisticsService.getOverview();
        return Result.success(overview);
    }

    /**
     * 获取文档趋势
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param type      趋势类型（create、view、like、favorite）
     * @return 趋势数据
     */
    @GetMapping("/trend/document")
    @Operation(summary = "文档趋势", description = "获取文档趋势数据")
    public Result<List<TrendVO>> getDocumentTrend(
        @Parameter(description = "开始日期", required = true)
        @RequestParam LocalDate startDate,
        @Parameter(description = "结束日期", required = true)
        @RequestParam LocalDate endDate,
        @Parameter(description = "趋势类型", required = true)
        @RequestParam String type) {
        log.info("获取文档趋势请求：startDate={}, endDate={}, type={}", startDate, endDate, type);

        List<TrendVO> trends = statisticsService.getDocumentTrend(startDate, endDate, type);
        return Result.success(trends);
    }

    /**
     * 获取用户活跃度
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 活跃度数据
     */
    @GetMapping("/activity/user")
    @Operation(summary = "用户活跃度", description = "获取用户活跃度统计")
    public Result<List<UserActivityVO>> getUserActivity(
        @Parameter(description = "开始日期", required = true)
        @RequestParam LocalDate startDate,
        @Parameter(description = "结束日期", required = true)
        @RequestParam LocalDate endDate) {
        log.info("获取用户活跃度请求：startDate={}, endDate={}", startDate, endDate);

        List<UserActivityVO> activities = statisticsService.getUserActivity(startDate, endDate);
        return Result.success(activities);
    }

    /**
     * 获取分类分布
     *
     * @return 分类分布数据
     */
    @GetMapping("/distribution/category")
    @Operation(summary = "分类分布", description = "获取文档分类分布统计")
    public Result<List<CategoryDistributionVO>> getCategoryDistribution() {
        log.info("获取分类分布请求");

        List<CategoryDistributionVO> distributions = statisticsService.getCategoryDistribution();
        return Result.success(distributions);
    }

    /**
     * 获取热门文档
     *
     * @param type 统计类型（view、like、favorite）
     * @param size 数量
     * @return 热门文档列表
     */
    @GetMapping("/hot/document")
    @Operation(summary = "热门文档", description = "获取热门文档排行")
    public Result<List<HotDocumentVO>> getHotDocuments(
        @Parameter(description = "统计类型", required = true)
        @RequestParam String type,
        @Parameter(description = "数量")
        @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取热门文档请求：type={}, size={}", type, size);

        List<HotDocumentVO> documents = statisticsService.getHotDocuments(type, size);
        return Result.success(documents);
    }

    /**
     * 获取最新文档
     *
     * @param size 数量
     * @return 最新文档列表
     */
    @GetMapping("/latest/documents")
    @Operation(summary = "最新文档", description = "获取最新发布的文档列表")
    public Result<List<HotDocumentVO>> getLatestDocuments(
        @Parameter(description = "数量")
        @RequestParam(defaultValue = "6") Integer size) {
        log.info("获取最新文档请求：size={}", size);

        List<HotDocumentVO> documents = statisticsService.getLatestDocuments(size);
        return Result.success(documents);
    }

    /**
     * 获取活跃用户
     *
     * @param type 统计类型（create、comment、view）
     * @param size 数量
     * @return 活跃用户列表
     */
    @GetMapping("/active/user")
    @Operation(summary = "活跃用户", description = "获取活跃用户排行")
    public Result<List<ActiveUserVO>> getActiveUsers(
        @Parameter(description = "统计类型", required = true)
        @RequestParam String type,
        @Parameter(description = "数量")
        @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取活跃用户请求：type={}, size={}", type, size);

        List<ActiveUserVO> users = statisticsService.getActiveUsers(type, size);
        return Result.success(users);
    }

    /**
     * 获取仪表盘数据
     *
     * @return 仪表盘数据
     */
    @GetMapping("/dashboard")
    @Operation(summary = "仪表盘数据", description = "获取仪表盘综合数据")
    public Result<DashboardVO> getDashboardData() {
        log.info("获取仪表盘数据请求");

        DashboardVO dashboard = statisticsService.getDashboardData();
        return Result.success(dashboard);
    }

    /**
     * 获取管理后台概览
     *
     * @return 管理后台概览数据
     */
    @GetMapping("/admin-overview")
    @Operation(summary = "管理后台概览", description = "获取管理后台仪表盘关键指标")
    public Result<AdminOverviewVO> getAdminOverview() {
        log.info("获取管理后台概览请求");

        AdminOverviewVO overview = statisticsService.getAdminOverview();
        return Result.success(overview);
    }
}
