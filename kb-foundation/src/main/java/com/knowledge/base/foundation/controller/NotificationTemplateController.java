package com.knowledge.base.foundation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.foundation.entity.NotificationTemplate;
import com.knowledge.base.foundation.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知模板管理Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/notifications/templates")
@Tag(name = "通知模板管理", description = "通知模板管理相关接口")
public class NotificationTemplateController {

    @Resource
    private NotificationTemplateService templateService;

    /**
     * 分页查询模板列表
     */
    @GetMapping
    @Operation(summary = "查询模板列表", description = "分页查询通知模板列表")
    public Result<PageResult<NotificationTemplate>> listTemplates(
            @Parameter(description = "当前页")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "通知类型（可选）")
            @RequestParam(required = false) String notificationType) {
        log.info("查询模板列表：current={}, size={}, notificationType={}", current, size, notificationType);

        IPage<NotificationTemplate> page = templateService.pageTemplates(current, size, notificationType);
        PageResult<NotificationTemplate> pageResult = new PageResult<>();
        pageResult.setRecords(page.getRecords());
        pageResult.setTotal(page.getTotal());
        pageResult.setCurrent(page.getCurrent());
        pageResult.setSize(page.getSize());
        return Result.success(pageResult);
    }

    /**
     * 获取所有启用的模板
     */
    @GetMapping("/active")
    @Operation(summary = "获取启用的模板", description = "获取所有启用状态的模板列表")
    public Result<List<NotificationTemplate>> listActiveTemplates() {
        log.info("获取启用的模板列表");

        List<NotificationTemplate> templates = templateService.listActiveTemplates();
        return Result.success(templates);
    }

    /**
     * 根据ID获取模板详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取模板详情", description = "根据ID获取通知模板详情")
    public Result<NotificationTemplate> getTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long id) {
        log.info("获取模板详情：id={}", id);

        NotificationTemplate template = templateService.getTemplateById(id);
        return Result.success(template);
    }

    /**
     * 创建模板
     */
    @PostMapping
    @Operation(summary = "创建模板", description = "创建新的通知模板")
    public Result<Boolean> createTemplate(
            @Parameter(description = "模板实体", required = true)
            @RequestBody NotificationTemplate template) {
        log.info("创建模板：templateCode={}", template.getTemplateCode());

        Boolean result = templateService.createTemplate(template);
        return Result.success(result);
    }

    /**
     * 更新模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新模板", description = "更新通知模板信息")
    public Result<Boolean> updateTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "模板实体", required = true)
            @RequestBody NotificationTemplate template) {
        log.info("更新模板：id={}", id);

        template.setId(id);
        Boolean result = templateService.updateTemplate(template);
        return Result.success(result);
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除模板", description = "删除指定的通知模板")
    public Result<Boolean> deleteTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long id) {
        log.info("删除模板：id={}", id);

        Boolean result = templateService.deleteTemplate(id);
        return Result.success(result);
    }

    /**
     * 测试发送
     */
    @PostMapping("/{id}/test")
    @Operation(summary = "测试发送", description = "测试发送通知模板")
    public Result<Boolean> testTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "测试目标", required = true)
            @RequestParam String target) {
        log.info("测试发送模板：id={}, target={}", id, target);

        Boolean result = templateService.testTemplate(id, target);
        return Result.success(result);
    }
}
