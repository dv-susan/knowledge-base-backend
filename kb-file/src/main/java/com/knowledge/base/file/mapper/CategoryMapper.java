package com.knowledge.base.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.file.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件分类Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据父分类ID查询子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有启用的分类
     *
     * @return 分类列表
     */
    List<Category> selectAllEnabled();

    /**
     * 查询分类及其所有子分类的ID
     *
     * @param parentId 父分类ID
     * @return 分类ID列表
     */
    List<Long> selectChildIds(@Param("parentId") Long parentId);
}
