package com.knowledge.base.foundation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.foundation.entity.Dict;
import com.knowledge.base.foundation.entity.DictData;
import com.knowledge.base.foundation.mapper.DictDataMapper;
import com.knowledge.base.foundation.mapper.DictMapper;
import com.knowledge.base.foundation.service.DictService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private DictMapper dictMapper;

    @Resource
    private DictDataMapper dictDataMapper;

    /** {@inheritDoc} */
    @Override
    public IPage<Dict> pageDicts(Long current, Long size, String keyword) {
        log.info("分页查询字典：current={}, size={}, keyword={}", current, size, keyword);

        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Dict::getDictCode, keyword)
                    .or()
                    .like(Dict::getDictName, keyword);
        }

        wrapper.orderByAsc(Dict::getSort);

        Page<Dict> page = new Page<>(current, size);
        return dictMapper.selectPage(page, wrapper);
    }

    /** {@inheritDoc} */
    @Override
    public Dict getDictByCode(String code) {
        log.info("根据编码获取字典：code={}", code);

        if (!StringUtils.hasText(code)) {
            throw new BusinessException("字典编码不能为空");
        }

        return dictMapper.selectByDictCode(code);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createDict(Dict dict) {
        log.info("创建字典：code={}, name={}", dict.getDictCode(), dict.getDictName());

        if (!StringUtils.hasText(dict.getDictCode())) {
            throw new BusinessException("字典编码不能为空");
        }
        if (!StringUtils.hasText(dict.getDictName())) {
            throw new BusinessException("字典名称不能为空");
        }

        Dict existDict = dictMapper.selectByDictCode(dict.getDictCode());
        if (existDict != null) {
            throw new BusinessException("字典编码已存在");
        }

        dict.setId(SnowflakeIdGenerator.getInstance().nextId());
        dict.setCreatedAt(LocalDateTime.now());
        dict.setUpdatedAt(LocalDateTime.now());

        int count = dictMapper.insert(dict);
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDict(String code, Dict dict) {
        log.info("更新字典：code={}", code);

        Dict existDict = dictMapper.selectByDictCode(code);
        if (existDict == null) {
            throw new BusinessException("字典不存在");
        }

        existDict.setDictName(dict.getDictName());
        existDict.setDescription(dict.getDescription());
        existDict.setSort(dict.getSort());
        existDict.setStatus(dict.getStatus());
        existDict.setUpdatedAt(LocalDateTime.now());

        int count = dictMapper.updateById(existDict);
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDict(String code) {
        log.info("删除字典：code={}", code);

        Dict existDict = dictMapper.selectByDictCode(code);
        if (existDict == null) {
            throw new BusinessException("字典不存在");
        }

        LambdaQueryWrapper<DictData> dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.eq(DictData::getDictId, existDict.getId());
        dictDataMapper.delete(dataWrapper);

        int count = dictMapper.deleteById(existDict.getId());
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    public List<DictData> getDictData(String code) {
        log.info("获取字典数据：code={}", code);

        if (!StringUtils.hasText(code)) {
            throw new BusinessException("字典编码不能为空");
        }

        return dictDataMapper.selectByDictCode(code);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addDictData(String code, DictData dictData) {
        log.info("添加字典数据：code={}, label={}", code, dictData.getDictLabel());

        Dict dict = dictMapper.selectByDictCode(code);
        if (dict == null) {
            throw new BusinessException("字典不存在");
        }

        dictData.setId(SnowflakeIdGenerator.getInstance().nextId());
        dictData.setDictId(dict.getId());
        dictData.setCreatedAt(LocalDateTime.now());

        int count = dictDataMapper.insert(dictData);
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDictData(String code, DictData dictData) {
        log.info("更新字典数据：code={}, id={}", code, dictData.getId());

        int count = dictDataMapper.updateById(dictData);
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDictData(String code, Long id) {
        log.info("删除字典数据：code={}, id={}", code, id);

        int count = dictDataMapper.deleteById(id);
        return count > 0;
    }
}