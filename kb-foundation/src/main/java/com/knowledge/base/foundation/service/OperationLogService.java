package com.knowledge.base.foundation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.foundation.entity.OperationLog;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作日志Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface OperationLogService {

    /**
     * 分页查询操作日志
     *
     * @param current       当前页
     * @param size          每页大小
     * @param module        模块（可选）
     * @param operationType 操作类型（可选）
     * @param username      用户名（可选）
     * @param startTime     开始时间（可选）
     * @param endTime       结束时间（可选）
     * @return 分页结果
     */
    IPage<OperationLog> pageLogs(Long current, Long size, String module, String operationType, String username, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据ID获取操作日志
     *
     * @param id 日志ID
     * @return 日志信息
     */
    OperationLog getLogById(Long id);

    /**
     * 获取操作日志统计信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    Map<String, Object> getStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除指定日期之前的操作日志
     *
     * @param beforeDate 截止日期
     * @return 删除数量
     */
    Integer deleteLogsBeforeDate(LocalDateTime beforeDate);
}
