package com.knowledge.base.document.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.service.UserFavoriteService;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.vo.UserFavoriteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户收藏Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/favorite")
@Tag(name = "用户收藏", description = "用户收藏管理接口")
public class UserFavoriteController {

    @Resource
    private UserFavoriteService userFavoriteService;

    /**
     * 切换收藏状态
     *
     * @param documentId 文档ID
     * @return 收藏状态（true-已收藏，false-未收藏）
     */
    @PostMapping("/toggle/{documentId}")
    @Operation(summary = "切换收藏状态", description = "切换文档的收藏状态")
    public Result<Boolean> toggleFavorite(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId) {
        log.info("切换收藏状态请求：documentId={}", documentId);

        Long userId = UserContext.getCurrentUserId();
        Boolean isFavorited = userFavoriteService.toggleFavorite(userId, documentId);
        return Result.success(isFavorited);
    }

    /**
     * 添加收藏
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @PostMapping("/add/{documentId}")
    @Operation(summary = "添加收藏", description = "添加文档到收藏")
    public Result<Boolean> addFavorite(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId) {
        log.info("添加收藏请求：documentId={}", documentId);

        Long userId = UserContext.getCurrentUserId();
        Boolean result = userFavoriteService.addFavorite(userId, documentId);
        return Result.success("添加收藏成功", result);
    }

    /**
     * 取消收藏
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @DeleteMapping("/remove/{documentId}")
    @Operation(summary = "取消收藏", description = "取消文档收藏")
    public Result<Boolean> removeFavorite(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId) {
        log.info("取消收藏请求：documentId={}", documentId);

        Long userId = UserContext.getCurrentUserId();
        Boolean result = userFavoriteService.removeFavorite(userId, documentId);
        return Result.success("取消收藏成功", result);
    }

    /**
     * 检查是否已收藏
     *
     * @param documentId 文档ID
     * @return 是否已收藏
     */
    @GetMapping("/check/{documentId}")
    @Operation(summary = "检查收藏状态", description = "检查文档是否已被收藏")
    public Result<Boolean> checkFavorite(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId) {
        log.info("检查收藏状态请求：documentId={}", documentId);

        Long userId = UserContext.getCurrentUserId();
        Boolean isFavorited = userFavoriteService.isFavorited(userId, documentId);
        return Result.success(isFavorited);
    }

    /**
     * 获取用户收藏列表
     *
     * @return 收藏列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取收藏列表", description = "获取当前用户的收藏列表")
    public Result<List<UserFavoriteVO>> getUserFavorites() {
        log.info("获取用户收藏列表请求");

        Long userId = UserContext.getCurrentUserId();
        List<UserFavoriteVO> favorites = userFavoriteService.getUserFavorites(userId);
        return Result.success(favorites);
    }

    /**
     * 获取文档收藏数量
     *
     * @param documentId 文档ID
     * @return 收藏数量
     */
    @GetMapping("/count/{documentId}")
    @Operation(summary = "获取收藏数量", description = "获取文档的收藏数量")
    public Result<Long> getFavoriteCount(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId) {
        log.info("获取收藏数量请求：documentId={}", documentId);

        Long count = userFavoriteService.getFavoriteCount(documentId);
        return Result.success(count);
    }
}
