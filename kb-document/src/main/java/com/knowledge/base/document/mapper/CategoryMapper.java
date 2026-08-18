package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据父分类ID查询子分类列表
     */
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据分类编码查询分类
     */
    Category selectByCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     * 根据状态查询分类列表
     */
    List<Category> selectByStatus(@Param("status") Integer status);

    /**
     * 查询所有根分类
     */
    List<Category> selectRootCategories();

    /**
     * 更新文档数量
     */
    int updateDocumentCount(@Param("categoryId") Long categoryId, @Param("count") Integer count);

    /**
     * 增加文档数量
     */
    int incrementDocumentCount(@Param("categoryId") Long categoryId);

    /**
     * 减少文档数量（不小于0）
     */
    int decrementDocumentCount(@Param("categoryId") Long categoryId);

    /**
     * 检查分类编码是否存在
     */
    boolean checkCategoryCodeExists(@Param("categoryCode") String categoryCode, @Param("id") Long id);
}
