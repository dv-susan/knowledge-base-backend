package com.knowledge.base.document.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.service.DocumentService;
import com.knowledge.base.document.service.DocumentShareService;
import com.knowledge.base.document.vo.DocumentVO;
import com.knowledge.base.document.vo.ShareVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 分享公开访问控制器
 *
 * <p>提供无需登录的分享链接访问接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/share")
@Tag(name = "分享公开访问", description = "无需登录的分享链接访问接口")
public class ShareController {

    @Resource
    private DocumentShareService documentShareService;

    @Resource
    private DocumentService documentService;

    @GetMapping("/{shareId}")
    @Operation(summary = "获取分享信息", description = "获取分享链接的基本信息，不增加访问计数")
    public Result<ShareVO> getShareInfo(
            @Parameter(description = "分享ID", required = true)
            @PathVariable String shareId) {
        log.info("公开访问获取分享信息：shareId={}", shareId);
        ShareVO shareVO = documentShareService.getShareById(shareId);
        // 隐藏敏感字段
        shareVO.setDocumentId(null);
        return Result.success(shareVO);
    }

    @PostMapping("/{shareId}/verify")
    @Operation(summary = "验证分享访问", description = "验证密码，不增加访问计数")
    public Result<Boolean> verifyShare(
            @Parameter(description = "分享ID", required = true)
            @PathVariable String shareId,
            @Parameter(description = "访问密码（可选）")
            @RequestParam(required = false) String password) {
        log.info("公开访问验证分享：shareId={}", shareId);
        boolean valid = documentShareService.verifyShareAccess(shareId, password);
        return Result.success(valid);
    }

    @PostMapping("/{shareId}/access")
    @Operation(summary = "访问分享", description = "验证并访问分享链接，增加访问计数，返回文档内容")
    public Result<DocumentVO> accessShare(
            @Parameter(description = "分享ID", required = true)
            @PathVariable String shareId,
            @Parameter(description = "访问密码（可选）")
            @RequestParam(required = false) String password) {
        log.info("公开访问分享链接：shareId={}", shareId);

        Long documentId = documentShareService.accessShare(shareId, password);
        DocumentVO document = documentService.viewDocument(documentId);

        log.info("公开访问分享成功：shareId={}, documentId={}", shareId, documentId);
        return Result.success(document);
    }
}
