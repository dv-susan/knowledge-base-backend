package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.document.dto.TagCreateDTO;
import com.knowledge.base.document.dto.TagQueryDTO;
import com.knowledge.base.document.dto.TagUpdateDTO;
import com.knowledge.base.document.entity.Tag;
import com.knowledge.base.document.vo.TagVO;

import java.util.List;

/**
 * 标签Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface TagService extends IService<Tag> {

    /**
     * 创建标签
     *
     * @param dto 创建DTO
     * @return 标签ID
     */
    Long createTag(TagCreateDTO dto);

    /**
     * 更新标签
     *
     * @param dto 更新DTO
     * @return 是否成功
     */
    Boolean updateTag(TagUpdateDTO dto);

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @return 是否成功
     */
    Boolean deleteTag(Long tagId);

    /**
     * 获取标签详情
     *
     * @param tagId 标签ID
     * @return 标签VO
     */
    TagVO getTagDetail(Long tagId);

    /**
     * 分页查询标签
     *
     * @param dto 查询DTO
     * @return 分页结果
     */
    PageResult<TagVO> pageTags(TagQueryDTO dto);

    /**
     * 获取热门标签
     *
     * @param limit 数量限制
     * @return 标签列表
     */
    List<TagVO> getHotTags(Integer limit);

    /**
     * 根据分类获取标签
     *
     * @param categoryId 分类ID
     * @return 标签列表
     */
    List<TagVO> getTagsByCategory(Long categoryId);

    /**
     * 批量创建标签
     *
     * @param tagNames 标签名称列表
     * @return 标签ID列表
     */
    List<Long> batchCreateTags(List<String> tagNames);
}
