package com.knowledge.base.document.controller;

import com.knowledge.base.common.annotation.OperationLog;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.BatchReviewDTO;
import com.knowledge.base.document.dto.DocumentReviewDTO;
import com.knowledge.base.document.dto.ReviewActionDTO;
import com.knowledge.base.document.dto.ReviewQueryDTO;
import com.knowledge.base.document.service.DocumentReviewService;
import com.knowledge.base.document.vo.DocumentReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文档审核Controller
 *
 * <p>映射路径 /review，通过网关 /api/document/review/** 访问。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Tag(name = "文档审核", description = "文档审核相关接口")
public class DocumentReviewController {

    private final DocumentReviewService reviewService;

    /**
     * 提交审核
     */
    @PostMapping("/submit/{documentId}")
    @Operation(summary = "提交文档审核", description = "提交文档进行审核")
    @OperationLog(module = "文档审核", operation = "提交审核", description = "提交文档审核")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_REVIEW)")
    public Result<Boolean> submitForReview(@PathVariable Long documentId) {
        Boolean result = reviewService.submitForReview(documentId);
        return Result.success(result);
    }

    /**
     * 获取审核任务列表（支持按状态筛选）
     */
    @GetMapping("/tasks")
    @Operation(summary = "获取审核任务列表", description = "分页查询审核任务，支持按状态筛选。审核员可查看全部，普通用户通过authorId参数查看自己的")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_REVIEW, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<PageResult<DocumentReviewVO>> getReviewTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long authorId) {
        ReviewQueryDTO dto = new ReviewQueryDTO();
        dto.setCurrent(page.longValue());
        dto.setSize(pageSize.longValue());
        dto.setKeyword(keyword);
        dto.setAuthorId(authorId);

        // 状态映射：pending→0, approved→1, rejected→2
        if (status != null) {
            dto.setStatus("pending".equals(status) ? 0 :
                          "approved".equals(status) ? 1 :
                          "rejected".equals(status) ? 2 : null);
        }

        PageResult<DocumentReviewVO> pageResult = reviewService.getPendingReviews(dto);
        return Result.success(pageResult);
    }

    /**
     * 获取文档当前审核任务
     */
    @GetMapping("/documents/{documentId}/current")
    @Operation(summary = "获取文档当前审核任务", description = "获取文档最新一条审核任务，用于独立审核页")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_REVIEW)")
    public Result<DocumentReviewVO> getCurrentReviewTask(@PathVariable Long documentId) {
        return Result.success(reviewService.getCurrentReviewTask(documentId));
    }

    /**
     * 获取待审核任务数量
     */
    @GetMapping("/tasks/pending-count")
    @Operation(summary = "获取待审核任务数量", description = "获取当前待审核的文档数量")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_REVIEW)")
    public Result<Long> getPendingCount() {
        Long count = reviewService.getPendingCount();
        return Result.success(count);
    }

    /**
     * 获取审核统计数据
     */
    @GetMapping("/tasks/stats")
    @Operation(summary = "获取审核统计数据", description = "获取待审核、已通过、已驳回数量")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_REVIEW)")
    public Result<Map<String, Long>> getReviewStats() {
        Map<String, Long> stats = reviewService.getReviewStats();
        return Result.success(stats);
    }

    /**
     * 单条审核（通过或驳回）
     */
    @PostMapping("/tasks/{taskId}/review")
    @Operation(summary = "审核文档", description = "对文档进行审核（通过或驳回）")
    @OperationLog(module = "文档审核", operation = "审核操作", description = "审核文档")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_REVIEW)")
    public Result<Boolean> reviewDocument(@PathVariable Long taskId,
                                           @Valid @RequestBody ReviewActionDTO dto) {
        DocumentReviewDTO reviewDTO = new DocumentReviewDTO();
        reviewDTO.setReviewId(taskId);
        reviewDTO.setReviewComment(dto.getComment());

        Boolean result;
        if ("approved".equalsIgnoreCase(dto.getStatus())) {
            reviewDTO.setReviewResult(1);
            result = reviewService.approveReview(reviewDTO);
        } else if ("rejected".equalsIgnoreCase(dto.getStatus())) {
            reviewDTO.setReviewResult(2);
            result = reviewService.rejectReview(reviewDTO);
        } else {
            return Result.error("无效的审核结果：" + dto.getStatus());
        }

        return Result.success(result);
    }

    /**
     * 批量审核（通过或驳回）
     */
    @PostMapping("/tasks/batch-review")
    @Operation(summary = "批量审核", description = "批量对文档进行审核（通过或驳回）")
    @OperationLog(module = "文档审核", operation = "批量审核", description = "批量审核文档")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_REVIEW)")
    public Result<String> batchReview(@Valid @RequestBody BatchReviewDTO dto) {
        reviewService.batchReview(dto.getTaskIds(), dto.getStatus(), dto.getComment());
        return Result.success("批量审核完成");
    }

    /**
     * 获取文档审核历史
     */
    @GetMapping("/documents/{documentId}/history")
    @Operation(summary = "获取审核历史", description = "获取文档的审核历史")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_REVIEW)")
    public Result<List<DocumentReviewVO>> getDocumentReviewHistory(@PathVariable Long documentId) {
        List<DocumentReviewVO> history = reviewService.getDocumentReviewHistory(documentId);
        return Result.success(history);
    }
}
