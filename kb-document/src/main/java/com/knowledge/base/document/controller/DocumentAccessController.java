package com.knowledge.base.document.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.service.DocumentAccessService;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.vo.DocumentAccessVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档访问记录Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/access")
@Tag(name = "文档访问记录", description = "文档访问记录管理接口")
@RequiredArgsConstructor
public class DocumentAccessController {

    private final DocumentAccessService documentAccessService;

    /**
     * 记录文档访问
     *
     * @param documentId 文档ID
     * @param documentTitle 文档标题
     * @return 是否成功
     */
    @PostMapping("/record")
    @Operation(summary = "记录文档访问", description = "记录用户访问文档的行为")
    public Result<Boolean> recordAccess(
            @Parameter(description = "文档ID", required = true)
            @RequestParam Long documentId,
            @Parameter(description = "文档标题", required = true)
            @RequestParam String documentTitle) {
        log.info("记录文档访问请求：documentId={}, documentTitle={}", documentId, documentTitle);

        Long userId = UserContext.getCurrentUserId();
        documentAccessService.recordAccess(userId, documentId, documentTitle);
        return Result.success(true);
    }

    /**
     * 获取用户最近访问记录
     *
     * @param limit 查询数量限制（默认20）
     * @return 访问记录列表
     */
    @GetMapping("/recent")
    @Operation(summary = "获取最近访问记录", description = "获取当前用户最近访问的文档列表")
    public Result<List<DocumentAccessVO>> getRecentAccess(
            @Parameter(description = "查询数量限制")
            @RequestParam(required = false) Integer limit) {
        log.info("获取最近访问记录请求：limit={}", limit);

        List<DocumentAccessVO> accessList = documentAccessService.getRecentAccess(limit);
        return Result.success(accessList);
    }

    /**
     * 删除单条访问记录
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @DeleteMapping("/remove/{documentId}")
    @Operation(summary = "删除访问记录", description = "删除单条访问记录")
    public Result<Boolean> deleteAccess(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId) {
        log.info("删除访问记录请求：documentId={}", documentId);

        documentAccessService.deleteAccess(documentId);
        return Result.success("删除成功", true);
    }

    /**
     * 清空用户所有访问记录
     *
     * @return 是否成功
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空访问记录", description = "清空当前用户的所有访问记录")
    public Result<Boolean> clearAllAccess() {
        log.info("清空访问记录请求");

        documentAccessService.clearAllAccess();
        return Result.success("清空成功", true);
    }
}
