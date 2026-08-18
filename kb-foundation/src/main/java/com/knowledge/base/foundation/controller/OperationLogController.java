package com.knowledge.base.foundation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.foundation.entity.OperationLog;
import com.knowledge.base.foundation.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作日志Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供操作日志管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/logs")
@Tag(name = "操作日志管理", description = "操作日志管理接口")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    /**
     * 分页查询日志列表
     *
     * @param current       当前页
     * @param size          每页大小
     * @param module        模块名称
     * @param operationType 操作类型
     * @param username      用户名
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return 日志分页信息
     */
    @GetMapping
    @Operation(summary = "分页查询日志", description = "分页查询操作日志列表")
    public Result<IPage<OperationLog>> pageLogs(
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
        @Parameter(description = "模块名称") @RequestParam(required = false) String module,
        @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
        @Parameter(description = "用户名") @RequestParam(required = false) String username,
        @Parameter(description = "开始时间") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
        @Parameter(description = "结束时间") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("分页查询日志请求：current={}, size={}, module={}, operationType={}, username={}",
            current, size, module, operationType, username);

        IPage<OperationLog> page = operationLogService.pageLogs(current, size, module,
            operationType, username, startTime, endTime);
        return Result.success(page);
    }

    /**
     * 根据ID查询日志详情
     *
     * @param id 日志ID
     * @return 日志详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询日志详情", description = "根据日志ID查询日志详情")
    public Result<OperationLog> getLogById(
        @Parameter(description = "日志ID", required = true)
        @PathVariable Long id) {
        log.info("查询日志详情请求：id={}", id);

        OperationLog log = operationLogService.getLogById(id);
        return Result.success(log);
    }

    /**
     * 获取日志统计信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取日志统计", description = "获取操作日志统计信息")
    public Result<Map<String, Object>> getStatistics(
        @Parameter(description = "开始时间") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
        @Parameter(description = "结束时间") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取日志统计请求：startTime={}, endTime={}", startTime, endTime);

        Map<String, Object> statistics = operationLogService.getStatistics(startTime, endTime);
        return Result.success(statistics);
    }

    /**
     * 删除指定日期前的日志
     *
     * @param beforeDate 指定日期
     * @return 删除数量
     */
    @DeleteMapping("/before-date")
    @Operation(summary = "删除历史日志", description = "删除指定日期前的操作日志")
    public Result<Integer> deleteLogsBeforeDate(
        @Parameter(description = "指定日期", required = true)
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beforeDate) {
        log.info("删除历史日志请求：beforeDate={}", beforeDate);

        Integer count = operationLogService.deleteLogsBeforeDate(beforeDate);
        return Result.success("删除成功", count);
    }
}
