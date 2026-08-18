package com.knowledge.base.document.constants;

/**
 * 文档服务权限点常量。
 *
 * <p>集中维护文档域权限编码，避免控制器层分散硬编码，
 * 便于权限治理、代码审查和后续统一重构。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public final class DocumentPermissionConstants {

    private DocumentPermissionConstants() {
    }

    /**
     * 文档查看与列表查询。
     */
    public static final String DOCUMENT_LIST = "document:list";

    /**
     * 创建文档。
     */
    public static final String DOCUMENT_CREATE = "document:create";

    /**
     * 编辑文档。
     */
    public static final String DOCUMENT_EDIT = "document:edit";

    /**
     * 删除文档。
     */
    public static final String DOCUMENT_DELETE = "document:delete";

    /**
     * 文档审核。
     */
    public static final String DOCUMENT_REVIEW = "document:review";

    /**
     * 分类管理。
     */
    public static final String DOCUMENT_CATEGORY = "document:category";

    /**
     * 分类查询。
     */
    public static final String DOCUMENT_CATEGORY_QUERY = "document:category:query";

    /**
     * 标签管理。
     */
    public static final String DOCUMENT_TAG = "document:tag";

    /**
     * 版本管理。
     */
    public static final String DOCUMENT_VERSION = "document:version";
}
