package com.knowledge.base.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.knowledge.base.common.result.PageResult;

/**
 * 分页工具类
 *
 * <p>提供 MyBatis-Plus 分页对象的便捷创建和转换方法</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class PageUtil {

    private PageUtil() {
    }

    /**
     * 创建 MyBatis-Plus 分页对象
     *
     * @param current 当前页
     * @param size    每页大小
     * @param <T>     实体类型
     * @return 分页对象
     */
    public static <T> Page<T> of(long current, long size) {
        return new Page<>(current, size);
    }

    /**
     * 将 MyBatis-Plus IPage 转换为项目通用 PageResult
     *
     * @param iPage MyBatis-Plus 分页结果
     * @param <T>   数据类型
     * @return 通用分页结果
     */
    public static <T> PageResult<T> toPageResult(IPage<T> iPage) {
        return PageResult.<T>builder()
                .current(iPage.getCurrent())
                .size(iPage.getSize())
                .total(iPage.getTotal())
                .pages(iPage.getPages())
                .records(iPage.getRecords())
                .build();
    }
}
