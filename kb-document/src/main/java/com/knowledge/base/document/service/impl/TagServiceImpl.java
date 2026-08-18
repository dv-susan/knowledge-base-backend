package com.knowledge.base.document.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.TagCreateDTO;
import com.knowledge.base.document.dto.TagQueryDTO;
import com.knowledge.base.document.dto.TagUpdateDTO;
import com.knowledge.base.document.entity.Tag;
import com.knowledge.base.document.mapper.TagMapper;
import com.knowledge.base.document.service.TagService;
import com.knowledge.base.document.vo.TagVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现标签相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTag(TagCreateDTO dto) {
        log.info("创建标签：tagName={}", dto.getTagName());

        // 检查标签名称是否已存在
        Tag existTag = tagMapper.selectOne(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getTagName, dto.getTagName())
        );
        if (existTag != null) {
            throw new BusinessException("标签名称已存在");
        }

        // 生成标签编码
        String tagCode = StringUtils.hasText(dto.getTagName())
                ? generateTagCode(dto.getTagName())
                : "TAG_" + System.currentTimeMillis();

        // 构建标签实体
        Tag tag = new Tag();
        tag.setId(SnowflakeIdGenerator.getInstance().nextId());
        tag.setTagName(dto.getTagName());
        tag.setTagCode(tagCode);
        tag.setCategoryId(dto.getCategoryId());
        tag.setTagType(dto.getTagType() != null ? dto.getTagType() : 1);
        tag.setColor(dto.getColor());
        tag.setIcon(dto.getIcon());
        tag.setDocCount(0);
        tag.setStatus(1);

        // 保存标签
        int count = tagMapper.insert(tag);
        if (count <= 0) {
            throw new BusinessException("创建标签失败");
        }

        return tag.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTag(TagUpdateDTO dto) {
        log.info("更新标签：tagId={}", dto.getId());

        if (dto.getId() == null) {
            throw new BusinessException("标签ID不能为空");
        }

        // 检查标签是否存在
        Tag existTag = tagMapper.selectById(dto.getId());
        if (existTag == null) {
            throw new BusinessException("标签不存在");
        }

        // 检查标签名称是否被其他标签使用
        if (StringUtils.hasText(dto.getTagName())
                && !dto.getTagName().equals(existTag.getTagName())) {
            Tag tag = tagMapper.selectOne(
                    new LambdaQueryWrapper<Tag>()
                            .eq(Tag::getTagName, dto.getTagName())
            );
            if (tag != null && !tag.getId().equals(dto.getId())) {
                throw new BusinessException("标签名称已被使用");
            }
        }

        // 构建更新实体
        Tag tag = new Tag();
        tag.setId(dto.getId());
        if (StringUtils.hasText(dto.getTagName())) {
            tag.setTagName(dto.getTagName());
        }
        if (dto.getCategoryId() != null) {
            tag.setCategoryId(dto.getCategoryId());
        }
        if (dto.getColor() != null) {
            tag.setColor(dto.getColor());
        }
        if (dto.getIcon() != null) {
            tag.setIcon(dto.getIcon());
        }
        if (dto.getStatus() != null) {
            tag.setStatus(dto.getStatus());
        }

        int count = tagMapper.updateById(tag);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTag(Long tagId) {
        log.info("删除标签：tagId={}", tagId);

        if (tagId == null) {
            throw new BusinessException("标签ID不能为空");
        }

        // 检查标签是否存在
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }

        // 检查是否有关联文档
        if (tag.getDocCount() != null && tag.getDocCount() > 0) {
            throw new BusinessException("该标签下有文档，不能删除");
        }

        // 删除标签
        int count = tagMapper.deleteById(tagId);
        return count > 0;
    }

    @Override
    public TagVO getTagDetail(Long tagId) {
        if (tagId == null) {
            throw new BusinessException("标签ID不能为空");
        }

        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }

        return convertToVO(tag);
    }

    @Override
    public PageResult<TagVO> pageTags(TagQueryDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getTagName())) {
            wrapper.like(Tag::getTagName, dto.getTagName())
                    .or()
                    .like(Tag::getTagCode, dto.getTagName());
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq(Tag::getCategoryId, dto.getCategoryId());
        }
        if (dto.getTagType() != null) {
            wrapper.eq(Tag::getTagType, dto.getTagType());
        }
        wrapper.eq(Tag::getStatus, 1);

        // 分页查询
        Page<Tag> page = new Page<>(dto.getCurrent(), dto.getSize());
        IPage<Tag> tagPage = tagMapper.selectPage(page, wrapper);

        // 转换为VO
        IPage<TagVO> voPage = tagPage.convert(this::convertToVO);

        return PageResult.<TagVO>builder()
                .records(voPage.getRecords())
                .total(voPage.getTotal())
                .current(voPage.getCurrent())
                .size(voPage.getSize())
                .build();
    }

    @Override
    public List<TagVO> getHotTags(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        List<Tag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getStatus, 1)
                        .orderByDesc(Tag::getDocCount)
                        .last("LIMIT " + limit)
        );

        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagVO> getTagsByCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }

        List<Tag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getCategoryId, categoryId)
                        .eq(Tag::getStatus, 1)
                        .orderByDesc(Tag::getDocCount)
        );

        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchCreateTags(List<String> tagNames) {
        log.info("批量创建标签：tagCount={}", tagNames.size());

        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> tagIds = new ArrayList<>();

        for (String tagName : tagNames) {
            if (!StringUtils.hasText(tagName)) {
                continue;
            }

            // 检查标签是否已存在
            Tag existTag = tagMapper.selectOne(
                    new LambdaQueryWrapper<Tag>()
                            .eq(Tag::getTagName, tagName.trim())
            );

            Long tagId;
            if (existTag != null) {
                tagId = existTag.getId();
            } else {
                // 创建新标签
                TagCreateDTO dto = new TagCreateDTO();
                dto.setTagName(tagName.trim());
                dto.setTagType(1); // 1-USER类型

                tagId = createTag(dto);
            }

            tagIds.add(tagId);
        }

        return tagIds;
    }

    /**
     * 转换为VO
     *
     * @param tag 标签实体
     * @return 标签VO
     */
    private TagVO convertToVO(Tag tag) {
        return TagVO.builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .tagCode(tag.getTagCode())
                .categoryId(tag.getCategoryId())
                .tagType(tag.getTagType())
                .color(tag.getColor())
                .icon(tag.getIcon())
                .docCount(tag.getDocCount() != null ? tag.getDocCount() : 0)
                .status(tag.getStatus())
                .createdAt(tag.getCreatedAt())
                .build();
    }

    /**
     * 生成标签编码
     *
     * @param tagName 标签名称
     * @return 标签编码
     */
    private String generateTagCode(String tagName) {
        // 简单的编码生成逻辑
        return "TAG_" + tagName.toUpperCase()
                .replaceAll("[^A-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "") + "_" + System.currentTimeMillis();
    }
}
