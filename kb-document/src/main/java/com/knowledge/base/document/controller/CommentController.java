package com.knowledge.base.document.controller;

import com.knowledge.base.common.annotation.OperationLog;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.CommentCreateDTO;
import com.knowledge.base.document.dto.CommentQueryDTO;
import com.knowledge.base.document.service.CommentService;
import com.knowledge.base.document.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "评论管理", description = "评论管理相关接口")
public class CommentController {

    private final CommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping
    @Operation(summary = "创建评论", description = "创建文档评论")
    @OperationLog(module = "评论管理", operation = "创建评论", description = "创建文档评论")
    public Result<Long> createComment(@Valid @RequestBody CommentCreateDTO dto) {
        Long commentId = commentService.createComment(dto);
        return Result.success(commentId);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "删除评论", description = "删除指定评论")
    @OperationLog(module = "评论管理", operation = "删除评论", description = "删除评论")
    public Result<Boolean> deleteComment(@PathVariable Long commentId) {
        Boolean result = commentService.deleteComment(commentId);
        return Result.success(result);
    }

    /**
     * 点赞评论
     */
    @PostMapping("/{commentId}/like")
    @Operation(summary = "点赞评论", description = "点赞指定评论")
    @OperationLog(module = "评论管理", operation = "点赞评论", description = "点赞评论")
    public Result<Boolean> likeComment(@PathVariable Long commentId) {
        Boolean result = commentService.likeComment(commentId);
        return Result.success(result);
    }

    /**
     * 取消点赞评论
     */
    @DeleteMapping("/{commentId}/like")
    @Operation(summary = "取消点赞评论", description = "取消点赞评论")
    @OperationLog(module = "评论管理", operation = "取消点赞", description = "取消点赞评论")
    public Result<Boolean> unlikeComment(@PathVariable Long commentId) {
        Boolean result = commentService.unlikeComment(commentId);
        return Result.success(result);
    }

    /**
     * 分页查询文档评论
     */
    @PostMapping("/document/{documentId}")
    @Operation(summary = "分页查询文档评论", description = "分页查询文档评论列表")
    public Result<PageResult<CommentVO>> pageDocumentComments(
            @PathVariable Long documentId,
            @RequestBody CommentQueryDTO dto) {
        PageResult<CommentVO> pageResult = commentService.pageDocumentComments(documentId, dto);
        return Result.success(pageResult);
    }

    /**
     * 获取评论回复列表
     */
    @GetMapping("/{parentCommentId}/replies")
    @Operation(summary = "获取评论回复", description = "获取评论的回复列表")
    public Result<List<CommentVO>> getCommentReplies(@PathVariable Long parentCommentId) {
        List<CommentVO> replies = commentService.getCommentReplies(parentCommentId);
        return Result.success(replies);
    }
}
