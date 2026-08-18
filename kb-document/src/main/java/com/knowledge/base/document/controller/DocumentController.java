package com.knowledge.base.document.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.BatchExportRequest;
import com.knowledge.base.document.dto.AutoSaveDTO;
import com.knowledge.base.document.dto.DocumentDTO;
import com.knowledge.base.document.dto.ShareDTO;
import com.knowledge.base.document.service.DocumentService;
import com.knowledge.base.document.service.DocumentShareService;
import com.knowledge.base.document.service.PdfExportService;
import com.knowledge.base.document.service.AutoSaveHistoryService;
import com.knowledge.base.document.vo.AutoSaveHistoryVO;
import com.knowledge.base.document.dto.AutoSaveHistoryQueryDTO;
import com.knowledge.base.document.vo.DocumentNeighborVO;
import com.knowledge.base.document.vo.DocumentVO;
import com.knowledge.base.document.vo.ShareVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 文档Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供文档管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/documents")
@Tag(name = "文档管理", description = "文档信息管理接口")
public class DocumentController {

    @Resource
    private DocumentService documentService;

    @Resource
    private PdfExportService pdfExportService;

    @Resource
    private DocumentShareService documentShareService;

    @Resource
    private AutoSaveHistoryService autoSaveHistoryService;

    /**
     * 创建文档
     *
     * @param documentDTO 文档信息
     * @return 文档ID
     */
    @PostMapping
    @Operation(summary = "创建文档", description = "创建新文档")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE)")
    public Result<Long> createDocument(@Valid @RequestBody DocumentDTO documentDTO) {
        log.info("创建文档请求：title={}", documentDTO.getTitle());

        Long documentId = documentService.createDocument(documentDTO);
        return Result.success("创建文档成功", documentId);
    }

    /**
     * 更新文档
     *
     * @param documentDTO 文档信息
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新文档", description = "更新文档信息")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> updateDocument(@Valid @RequestBody DocumentDTO documentDTO) {
        log.info("更新文档请求：documentId={}", documentDTO.getId());

        Boolean success = documentService.updateDocument(documentDTO);
        return Result.success("更新文档成功", success);
    }

    /**
     * 更新文档摘要（部分更新，不受全量校验约束）
     *
     * @param documentId 文档ID
     * @param body       请求体，包含summary字段
     * @return 是否成功
     */
    @PatchMapping("/{documentId}/summary")
    @Operation(summary = "更新文档摘要", description = "仅更新文档的summary字段")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> updateSummary(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId,
            @RequestBody Map<String, String> body) {
        String summary = body.get("summary");
        if (summary == null || summary.isBlank()) {
            return Result.error("摘要内容不能为空");
        }
        if (summary.length() > 500) {
            summary = summary.substring(0, 500);
        }
        log.info("更新文档摘要请求：documentId={}", documentId);
        Boolean success = documentService.updateSummary(documentId, summary);
        return Result.success("摘要更新成功", success);
    }

    /**
     * 自动保存文档（创建或更新草稿）
     *
     * <p>允许空标题，强制状态为草稿，不触发RAG/KAG/ES索引。
     * 用于前端编辑器自动保存场景，防止用户数据丢失。</p>
     *
     * @param autoSaveDTO 自动保存数据
     * @return 文档ID
     */
    @PostMapping("/autosave")
    @Operation(summary = "自动保存文档", description = "自动保存草稿，允许空标题，不触发索引")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Long> autoSaveDocument(@Valid @RequestBody AutoSaveDTO autoSaveDTO) {
        log.info("自动保存请求：id={}, title={}", autoSaveDTO.getId(), autoSaveDTO.getTitle());
        Long documentId = documentService.autoSaveDocument(autoSaveDTO);
        return Result.success("自动保存成功", documentId);
    }

    /**
     * 放弃自动保存草稿：将当前用户所有草稿标记为已确认
     *
     * <p>用户点击"放弃草稿"后调用，标记 autoSaveDismissed=1，
     * 后续进入新建文档页不再弹出恢复提示。</p>
     *
     * @return 是否成功
     */
    @PutMapping("/autosave/dismiss")
    @Operation(summary = "放弃自动保存草稿", description = "将当前用户所有草稿标记为已确认，不再弹出恢复提示")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> dismissAutoSaveDrafts() {
        log.info("放弃自动保存草稿请求");
        documentService.dismissAutoSaveDrafts();
        return Result.success("已确认放弃草稿", true);
    }

    /**
     * 删除文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @DeleteMapping("/{documentId}")
    @Operation(summary = "删除文档", description = "根据文档ID删除文档")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_DELETE)")
    public Result<Boolean> deleteDocument(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("删除文档请求：documentId={}", documentId);

        Boolean success = documentService.deleteDocument(documentId);
        return Result.success("删除文档成功", success);
    }

    /**
     * 根据ID查询文档
     *
     * @param documentId 文档ID
     * @return 文档信息
     */
    @GetMapping("/{documentId}")
    @Operation(summary = "查询文档", description = "根据文档ID查询文档详情")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<DocumentVO> getDocumentById(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("查询文档请求：documentId={}", documentId);

        DocumentVO documentVO = documentService.getDocumentById(documentId);
        return Result.success(documentVO);
    }

    /**
     * 浏览文档（增加浏览次数）
     *
     * @param documentId 文档ID
     * @return 文档信息
     */
    @GetMapping("/{documentId}/view")
    @Operation(summary = "浏览文档", description = "浏览文档并增加浏览次数")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<DocumentVO> viewDocument(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("浏览文档请求：documentId={}", documentId);

        DocumentVO documentVO = documentService.viewDocument(documentId);
        return Result.success(documentVO);
    }

    /**
     * 分页查询文档列表
     *
     * @param current    当前页
     * @param size       每页大小
     * @param categoryId 分类ID
     * @param keyword    搜索关键词
     * @param status     状态
     * @param sortBy     排序字段
     * @param sortOrder  排序方向
     * @return 文档分页信息
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询文档", description = "分页查询文档列表")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<IPage<DocumentVO>> pageDocuments(
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
        @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
        @Parameter(description = "团队空间ID") @RequestParam(required = false) Long teamId,
        @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
        @Parameter(description = "状态") @RequestParam(required = false) Integer status,
        @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
        @Parameter(description = "排序方向") @RequestParam(required = false) String sortOrder,
        @Parameter(description = "作者ID") @RequestParam(required = false) Long authorId) {
        log.info("分页查询文档请求：current={}, size={}, categoryId={}, keyword={}, status={}, sortBy={}, sortOrder={}, authorId={}",
            current, size, categoryId, keyword, status, sortBy, sortOrder, authorId);

        IPage<DocumentVO> page = documentService.pageDocuments(current, size, categoryId, teamId, keyword, status, sortBy, sortOrder, authorId);
        return Result.success(page);
    }

    /**
     * 查询文档的上一篇和下一篇
     *
     * @param documentId 文档ID
     * @return 相邻文档信息
     */
    @GetMapping("/{documentId}/neighbors")
    @Operation(summary = "查询相邻文档", description = "查询文档的上一篇和下一篇，用于详情页导航")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<DocumentNeighborVO> getDocumentNeighbors(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("查询相邻文档请求：documentId={}", documentId);
        DocumentNeighborVO result = documentService.getDocumentNeighbors(documentId);
        return Result.success(result);
    }

    /**
     * 上传文档文件
     *
     * @param file 文件
     * @return 文件路径
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文档文件", description = "上传文档文件并返回文件路径")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<String> uploadDocumentFile(
        @Parameter(description = "文件", required = true)
        @RequestParam("file") MultipartFile file) {
        log.info("上传文档文件请求：fileName={}", file.getOriginalFilename());

        String filePath = documentService.uploadDocumentFile(file);
        return Result.success("上传文件成功", filePath);
    }

    /**
     * 上传文件并解析创建文档
     *
     * <p>上传文件 → 解析文本内容 → 自动创建文档记录 → 发布走管线</p>
     *
     * @param file 文件
     * @return 文档创建结果（包含 documentId / title / fileUrl / fileSize / contentLength / contentPreview）
     */
    @PostMapping("/upload/parse")
    @Operation(summary = "上传文件并解析创建文档", description = "上传文件后自动解析内容并创建文档记录，直接发布走知识管线")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Map<String, Object>> uploadAndParseDocument(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file) {
        log.info("上传并解析文档请求：fileName={}, size={}", file.getOriginalFilename(), file.getSize());

        Map<String, Object> result = documentService.uploadAndCreateDocument(file);
        return Result.success("文件解析并创建文档成功", result);
    }

    /**
     * 点赞文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @PostMapping("/{documentId}/like")
    @Operation(summary = "点赞文档", description = "用户点赞文档")
    public Result<Boolean> likeDocument(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("点赞文档请求：documentId={}", documentId);

        Boolean success = documentService.likeDocument(documentId);
        return Result.success("点赞成功", success);
    }

    /**
     * 取消点赞文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @DeleteMapping("/{documentId}/like")
    @Operation(summary = "取消点赞文档", description = "用户取消点赞文档")
    public Result<Boolean> unlikeDocument(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("取消点赞文档请求：documentId={}", documentId);

        Boolean success = documentService.unlikeDocument(documentId);
        return Result.success("取消点赞成功", success);
    }

    /**
     * 收藏文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @PostMapping("/{documentId}/favorite")
    @Operation(summary = "收藏文档", description = "用户收藏文档")
    public Result<Boolean> favoriteDocument(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("收藏文档请求：documentId={}", documentId);

        Boolean success = documentService.favoriteDocument(documentId);
        return Result.success("收藏成功", success);
    }

    /**
     * 发布文档（根据系统配置决定直接发布或提交审核流程）
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @PutMapping("/{documentId}/publish")
    @Operation(summary = "发布文档", description = "根据系统配置直接发布或提交审核流程")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> publishDocument(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("提交文档审核请求：documentId={}", documentId);

        Boolean success = documentService.publishDocument(documentId);
        return Result.success(success ? "发布成功" : "发布失败", success);
    }

    /**
     * 归档文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @PutMapping("/{documentId}/archive")
    @Operation(summary = "归档文档", description = "归档文档")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> archiveDocument(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("归档文档请求：documentId={}", documentId);

        Boolean success = documentService.archiveDocument(documentId);
        return Result.success("归档成功", success);
    }

    /**
     * 导出文档为PDF
     *
     * @param documentId 文档ID
     * @return PDF下载URL
     */
    @GetMapping("/{documentId}/export-pdf")
    @Operation(summary = "导出PDF", description = "导出文档为PDF并返回下载链接")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<String> exportDocumentToPdf(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("导出PDF请求：documentId={}", documentId);

        String pdfUrl = pdfExportService.exportDocumentToPdf(documentId);
        return Result.success("PDF导出成功", pdfUrl);
    }

    /**
     * 下载文档PDF
     *
     * @param documentId 文档ID
     * @param response HTTP响应
     */
    @GetMapping("/{documentId}/download-pdf")
    @Operation(summary = "下载PDF", description = "直接下载文档PDF文件")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public void downloadDocumentPdf(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId,
        HttpServletResponse response) {
        log.info("下载PDF请求：documentId={}", documentId);

        try {
            byte[] pdfBytes = pdfExportService.exportDocumentToPdfBytes(documentId);

            DocumentVO document = documentService.getDocumentById(documentId);
            String fileName = pdfExportService.generatePdfFileName(documentId, document.getTitle());

            response.setContentType("application/pdf");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + encodedFileName);
            response.setContentLength(pdfBytes.length);
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

            log.info("PDF下载成功：documentId={}", documentId);
        } catch (IOException e) {
            log.error("PDF下载失败：documentId={}", documentId, e);
            throw new RuntimeException("PDF下载失败：" + e.getMessage());
        }
    }

    /**
     * 批量导出文档
     *
     * @param request 批量导出请求
     * @param response HTTP响应
     */
    @PostMapping("/batch-export")
    @Operation(summary = "批量导出", description = "批量导出选中文档为PDF或Markdown格式的ZIP文件")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public void batchExportDocuments(
            @Parameter(description = "批量导出请求", required = true)
            @Valid @RequestBody BatchExportRequest request,
            HttpServletResponse response) {
        log.info("批量导出请求：documentIds={}, format={}", request.getDocumentIds(), request.getFormat());

        try {
            byte[] zipBytes = pdfExportService.batchExportDocuments(
                    request.getDocumentIds(), request.getFormat());

            String fileName = "documents_export_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".zip";

            response.setContentType("application/zip");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + encodedFileName);
            response.setContentLength(zipBytes.length);
            response.getOutputStream().write(zipBytes);
            response.getOutputStream().flush();

            log.info("批量导出成功：共{}个文档", request.getDocumentIds().size());
        } catch (IOException e) {
            log.error("批量导出失败", e);
            throw new RuntimeException("批量导出失败：" + e.getMessage());
        }
    }

    /**
     * 创建分享链接
     *
     * @param shareDTO 分享参数
     * @return 分享信息
     */
    @PostMapping("/share")
    @Operation(summary = "创建分享", description = "创建文档分享链接")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<ShareVO> createShare(@Valid @RequestBody ShareDTO shareDTO) {
        log.info("创建分享链接请求：documentId={}", shareDTO.getDocumentId());

        ShareVO shareVO = documentShareService.createShare(shareDTO);
        return Result.success("分享链接创建成功", shareVO);
    }

    /**
     * 获取分享信息
     *
     * @param shareId 分享ID
     * @return 分享信息
     */
    @GetMapping("/share/{shareId}")
    @Operation(summary = "获取分享信息", description = "通过分享ID获取分享信息")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<ShareVO> getShareInfo(
        @Parameter(description = "分享ID", required = true)
        @PathVariable String shareId) {
        log.info("获取分享信息请求：shareId={}", shareId);

        ShareVO shareVO = documentShareService.getShareById(shareId);
        return Result.success(shareVO);
    }

    /**
     * 访问分享链接
     *
     * @param shareId 分享ID
     * @param password 访问密码（可选）
     * @return 文档ID
     */
    @PostMapping("/share/{shareId}/access")
    @Operation(summary = "访问分享", description = "访问分享链接并返回文档ID")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<Long> accessShare(
        @Parameter(description = "分享ID", required = true)
        @PathVariable String shareId,
        @Parameter(description = "访问密码")
        @RequestParam(required = false) String password) {
        log.info("访问分享链接请求：shareId={}", shareId);

        Long documentId = documentShareService.accessShare(shareId, password);
        return Result.success(documentId);
    }

    /**
     * 获取文档的所有分享
     *
     * @param documentId 文档ID
     * @return 分享列表
     */
    @GetMapping("/{documentId}/shares")
    @Operation(summary = "获取文档分享列表", description = "获取文档的所有分享链接")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<List<ShareVO>> getDocumentShares(
        @Parameter(description = "文档ID", required = true)
        @PathVariable Long documentId) {
        log.info("获取文档分享列表请求：documentId={}", documentId);

        List<ShareVO> shares = documentShareService.getSharesByDocumentId(documentId);
        return Result.success(shares);
    }

    /**
     * 获取我的分享列表
     *
     * @return 分享列表
     */
    @GetMapping("/share/my")
    @Operation(summary = "获取我的分享", description = "获取当前用户的分享列表")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<List<ShareVO>> getMyShares() {
        log.info("获取我的分享列表请求");

        List<ShareVO> shares = documentShareService.getMyShares();
        return Result.success(shares);
    }

    /**
     * 删除分享链接
     *
     * @param shareId 分享ID
     * @return 是否成功
     */
    @DeleteMapping("/share/{shareId}")
    @Operation(summary = "删除分享", description = "删除分享链接")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> deleteShare(
        @Parameter(description = "分享ID", required = true)
        @PathVariable String shareId) {
        log.info("删除分享链接请求：shareId={}", shareId);

        boolean success = documentShareService.deleteShare(shareId);
        return Result.success("分享已删除", success);
    }

    /**
     * 更新分享设置
     *
     * @param shareId 分享ID
     * @param shareDTO 更新参数
     * @return 是否成功
     */
    @PutMapping("/share/{shareId}")
    @Operation(summary = "更新分享设置", description = "更新分享链接的设置")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> updateShare(
        @Parameter(description = "分享ID", required = true)
        @PathVariable String shareId,
        @RequestBody ShareDTO shareDTO) {
        log.info("更新分享设置请求：shareId={}", shareId);

        boolean success = documentShareService.updateShare(shareId, shareDTO);
        return Result.success("分享设置已更新", success);
    }

    /**
     * 清理知识图谱脏节点
     *
     * <p>删除 Neo4j 中 MySQL 已不存在文档的图谱节点，解决因异步删除失败导致的脏数据问题。</p>
     *
     * @return 操作结果
     */
    @PostMapping("/graph/cleanup")
    @Operation(summary = "清理知识图谱脏节点", description = "删除MySQL中已删除但Neo4j中残留的文档图谱节点")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<String> cleanupGraphGhostNodes() {
        log.info("清理知识图谱脏节点请求");
        int count = documentService.cleanupGraphGhostNodes();
        return Result.success("同步了 " + count + " 个有效文档ID，脏节点清理请求已发送");
    }

    /**
     * 批量重建所有已发布文档的知识图谱
     *
     * <p>遍历所有已发布文档，触发 KAG 图谱构建，用于首次建图或全量重建。</p>
     *
     * @return 操作结果
     */
    @PostMapping("/graph/rebuild")
    @Operation(summary = "批量重建知识图谱", description = "重建所有已发布文档的知识图谱")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<String> rebuildAllGraphs() {
        log.info("批量重建知识图谱请求");
        int count = documentService.rebuildAllGraphs();
        return Result.success("已触发 " + count + " 篇文档的知识图谱重建，请稍后在知识图谱页面查看");
    }

    /**
     * 获取文档的自动保存历史
     *
     * @param documentId 文档ID
     * @param current    当前页
     * @param size       每页大小
     * @return 分页历史快照
     */
    @GetMapping("/{documentId}/autosave-history")
    @Operation(summary = "获取自动保存历史", description = "分页查询指定文档的自动保存快照历史")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<IPage<AutoSaveHistoryVO>> getAutoSaveHistory(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId,
            @Parameter(description = "当前页", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Long size) {
        log.info("查询自动保存历史：documentId={}, current={}, size={}", documentId, current, size);
        AutoSaveHistoryQueryDTO query = new AutoSaveHistoryQueryDTO();
        query.setDocumentId(documentId);
        query.setCurrent(current);
        query.setSize(size);
        IPage<AutoSaveHistoryVO> result = autoSaveHistoryService.pageHistory(query);
        return Result.success(result);
    }

    /**
     * 获取单个自动保存快照详情（含完整Markdown内容）
     *
     * @param documentId 文档ID
     * @param snapshotId 快照ID（MongoDB _id）
     * @return 快照详情
     */
    @GetMapping("/{documentId}/autosave-history/{snapshotId}")
    @Operation(summary = "获取自动保存快照详情", description = "根据快照ID获取完整Markdown内容")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<AutoSaveHistoryVO> getAutoSaveSnapshot(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long documentId,
            @Parameter(description = "快照ID（MongoDB _id）", required = true)
            @PathVariable String snapshotId) {
        log.info("查询自动保存快照详情：documentId={}, snapshotId={}", documentId, snapshotId);
        AutoSaveHistoryVO vo = autoSaveHistoryService.getSnapshot(snapshotId, documentId);
        return Result.success(vo);
    }
}
