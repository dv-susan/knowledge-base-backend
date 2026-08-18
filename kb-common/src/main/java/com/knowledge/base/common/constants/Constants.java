package com.knowledge.base.common.constants;

/**
 * 系统常量
 *
 * @author 苏三
 * @since 1.0.0
 */
public class Constants {

    /**
     * UTF-8编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Token请求头名称
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Token缓存前缀
     */
    public static final String TOKEN_CACHE_PREFIX = "token:";

    /**
     * 用户缓存前缀
     */
    public static final String USER_CACHE_PREFIX = "user:";

    /**
     * 文档缓存前缀
     */
    public static final String DOC_CACHE_PREFIX = "doc:";

    /**
     * 分类缓存前缀
     */
    public static final String CATEGORY_CACHE_PREFIX = "category:";

    /**
     * 标签缓存前缀
     */
    public static final String TAG_CACHE_PREFIX = "tag:";

    /**
     * 权限缓存前缀
     */
    public static final String PERMISSION_CACHE_PREFIX = "permission:";

    /**
     * 分布式锁前缀
     */
    public static final String LOCK_PREFIX = "lock:";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 超级管理员ID
     */
    public static final Long SUPER_ADMIN_ID = 1000000000000000001L;

    /**
     * 系统用户ID
     */
    public static final Long SYSTEM_USER_ID = 0L;

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
