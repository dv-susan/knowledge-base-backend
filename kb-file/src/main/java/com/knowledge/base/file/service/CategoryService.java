package com.knowledge.base.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.file.dto.CategoryDTO;
import com.knowledge.base.file.entity.Category;
import com.knowledge.base.file.vo.CategoryVO;

import java.util.List;

/**
 * 文件分类服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface CategoryService extends IService<Category> {

    /**
     * 创建分类
     *
     * @param dto 分类请求
     * @return 创建后的分类VO
     */
    CategoryVO create(CategoryDTO dto);

    /**
     * 更新分类
     *
     * @param dto 分类请求
     * @return 更新后的分类VO
     */
    CategoryVO update(CategoryDTO dto);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void delete(Long id);

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类VO
     */
    CategoryVO getById(Long id);

    /**
     * 获取所有分类（平铺列表）
     *
     * @return 分类列表
     */
    List<CategoryVO> listAll();

    /**
     * 获取分类树结构
     *
     * @return 分类树
     */
    List<CategoryVO> getTree();

    /**
     * 根据父分类ID获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<CategoryVO> listByParentId(Long parentId);

    /**
     * 启用/禁用分类
     *
     * @param id     分类ID
     * @param status 状态
     * @return 更新后的分类VO
     */
    CategoryVO updateStatus(Long id, Integer status);
}
