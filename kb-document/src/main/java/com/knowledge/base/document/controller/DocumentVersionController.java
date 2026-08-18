package com.knowledge.base.document.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.DocumentVersionRestoreDTO;
import com.knowledge.base.document.service.DocumentVersionService;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.vo.DocumentVersionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 文档版本管理Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供文档版本管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/documents/{documentId}/versions")
@Tag(name = "文档版本管理", description = "文档版本管理接口")
public class DocumentVersionController {

    @Resource
    private DocumentVersionService documentVersionService;

    /**
     * 获取文档版本列表
     *
     * @param documentId 文档ID
     * @param current    当前页
     * @param size       每页大小
     * @return 版本分页信息
     */
    @GetMapping
    @Operation(summary = "获取文档版本列表", description = "分页查询文档版本列表")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_VERSION)")
    public Result<IPage<DocumentVersionVO>> getVersions(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId,
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size) {
        log.info("获取文档版本列表请求：documentId={}, current={}, size={}", documentId, current, size);

        IPage<DocumentVersionVO> page = documentVersionService.getVersionList(documentId, current, size);
        return Result.success(page);
    }

    /**
     * 获取文档版本详情
     *
     * @param documentId 文档ID
     * @param versionId  版本ID
     * @return 版本详情
     */
    @GetMapping("/{versionId}")
    @Operation(summary = "获取文档版本详情", description = "根据版本ID获取版本详情")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_VERSION)")
    public Result<DocumentVersionVO> getVersionDetail(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId,
        @Parameter(description = "版本ID", required = true)
        @PathVariable Long versionId) {
        log.info("获取文档版本详情请求：documentId={}, versionId={}", documentId, versionId);

        DocumentVersionVO versionVO = documentVersionService.getVersionDetail(versionId);
        return Result.success(versionVO);
    }

    /**
     * 恢复文档版本
     *
     * @param documentId 文档ID
     * @param dto        恢复版本DTO
     * @return 是否成功
     */
    @PostMapping("/restore")
    @Operation(summary = "恢复文档版本", description = "将文档恢复到指定版本")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_VERSION)")
    public Result<Boolean> restoreVersion(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId,
        @Valid @RequestBody DocumentVersionRestoreDTO dto) {
        log.info("恢复文档版本请求：documentId={}, versionId={}", documentId, dto.getVersionId());

        Long userId = UserContext.getCurrentUserId();
        Boolean success = documentVersionService.restoreVersion(documentId, dto, userId);
        return Result.success("恢复版本成功", success);
    }

    /**
     * 对比两个版本
     *
     * @param documentId 文档ID
     * @param versionId1 版本1 ID
     * @param versionId2 版本2 ID
     * @return 对比结果
     */
    @GetMapping("/compare")
    @Operation(summary = "对比文档版本", description = "对比两个文档版本的差异")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_VERSION)")
    public Result<String> compareVersions(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId,
        @Parameter(description = "版本1 ID", required = true)
        @RequestParam Long versionId1,
        @Parameter(description = "版本2 ID", required = true)
        @RequestParam Long versionId2) {
        log.info("对比文档版本请求：documentId={}, versionId1={}, versionId2={}",
            documentId, versionId1, versionId2);

        String diff = documentVersionService.compareVersions(versionId1, versionId2);
        return Result.success(diff);
    }
}
