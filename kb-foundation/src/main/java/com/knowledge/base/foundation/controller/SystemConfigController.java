package com.knowledge.base.foundation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.foundation.entity.SystemConfig;
import com.knowledge.base.foundation.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供系统配置管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/config")
@Tag(name = "系统配置管理", description = "系统配置管理接口")
public class SystemConfigController {

    @Resource
    private SystemConfigService systemConfigService;

    /**
     * 分页查询配置列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param category 配置分类
     * @return 配置分页信息
     */
    @GetMapping
    @Operation(summary = "分页查询配置", description = "分页查询系统配置列表")
    public Result<IPage<SystemConfig>> pageConfigs(
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
        @Parameter(description = "配置分类") @RequestParam(required = false) String category) {
        log.info("分页查询配置请求：current={}, size={}, category={}", current, size, category);

        IPage<SystemConfig> page = systemConfigService.pageConfigs(current, size, category);
        return Result.success(page);
    }

    /**
     * 根据配置键查询配置项
     *
     * @param key 配置键
     * @return 配置信息
     */
    @GetMapping("/{key}")
    @Operation(summary = "查询配置项", description = "根据配置键查询配置项")
    public Result<SystemConfig> getConfigByKey(
        @Parameter(description = "配置键", required = true)
        @PathVariable String key) {
        log.info("查询配置项请求：key={}", key);

        SystemConfig config = systemConfigService.getConfigByKey(key);
        return Result.success(config);
    }

    /**
     * 创建配置
     *
     * @param config 配置信息
     * @return 是否成功
     */
    @PostMapping
    @Operation(summary = "创建配置", description = "创建新的系统配置")
    public Result<Boolean> createConfig(@Valid @RequestBody SystemConfig config) {
        log.info("创建配置请求：key={}", config.getConfigKey());

        Boolean success = systemConfigService.createConfig(config);
        return Result.success("创建配置成功", success);
    }

    /**
     * 更新配置
     *
     * @param key    配置键
     * @param config 配置信息
     * @return 是否成功
     */
    @PutMapping("/{key}")
    @Operation(summary = "更新配置", description = "更新系统配置")
    public Result<Boolean> updateConfig(
        @Parameter(description = "配置键", required = true)
        @PathVariable String key,
        @Valid @RequestBody SystemConfig config) {
        log.info("更新配置请求：key={}", key);

        Boolean success = systemConfigService.updateConfig(key, config);
        return Result.success("更新配置成功", success);
    }

    /**
     * 删除配置
     *
     * @param key 配置键
     * @return 是否成功
     */
    @DeleteMapping("/{key}")
    @Operation(summary = "删除配置", description = "根据配置键删除配置")
    public Result<Boolean> deleteConfig(
        @Parameter(description = "配置键", required = true)
        @PathVariable String key) {
        log.info("删除配置请求：key={}", key);

        Boolean success = systemConfigService.deleteConfig(key);
        return Result.success("删除配置成功", success);
    }

    /**
     * 按分类获取配置
     *
     * @param category 配置分类
     * @return 配置列表
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "按分类获取配置", description = "根据配置分类获取配置列表")
    public Result<List<SystemConfig>> getConfigsByCategory(
        @Parameter(description = "配置分类", required = true)
        @PathVariable String category) {
        log.info("按分类获取配置请求：category={}", category);

        List<SystemConfig> configs = systemConfigService.getConfigsByCategory(category);
        return Result.success(configs);
    }

    /**
     * 获取公开配置
     *
     * @return 公开配置列表
     */
    @GetMapping("/public")
    @Operation(summary = "获取公开配置", description = "获取所有公开的系统配置")
    public Result<List<SystemConfig>> getPublicConfigs() {
        log.info("获取公开配置请求");

        List<SystemConfig> configs = systemConfigService.getPublicConfigs();
        return Result.success(configs);
    }
}
