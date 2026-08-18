package com.knowledge.base.foundation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.foundation.dto.DictDTO;
import com.knowledge.base.foundation.entity.Dict;
import com.knowledge.base.foundation.entity.DictData;
import com.knowledge.base.foundation.vo.DictDataVO;
import com.knowledge.base.foundation.vo.DictVO;

import java.util.List;

/**
 * 字典Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface DictService {

    /**
     * 分页查询字典
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 关键字（可选）
     * @return 分页结果
     */
    IPage<Dict> pageDicts(Long current, Long size, String keyword);

    /**
     * 根据字典编码获取字典
     *
     * @param code 字典编码
     * @return 字典信息
     */
    Dict getDictByCode(String code);

    /**
     * 创建字典
     *
     * @param dict 字典信息
     * @return 是否成功
     */
    Boolean createDict(Dict dict);

    /**
     * 更新字典
     *
     * @param code 字典编码
     * @param dict 新字典信息
     * @return 是否成功
     */
    Boolean updateDict(String code, Dict dict);

    /**
     * 删除字典
     *
     * @param code 字典编码
     * @return 是否成功
     */
    Boolean deleteDict(String code);

    /**
     * 获取字典数据列表
     *
     * @param code 字典编码
     * @return 字典数据列表
     */
    List<DictData> getDictData(String code);

    /**
     * 添加字典数据项
     *
     * @param code     字典编码
     * @param dictData 字典数据
     * @return 是否成功
     */
    Boolean addDictData(String code, DictData dictData);

    /**
     * 更新字典数据项
     *
     * @param code     字典编码
     * @param dictData 字典数据
     * @return 是否成功
     */
    Boolean updateDictData(String code, DictData dictData);

    /**
     * 删除字典数据项
     *
     * @param code 字典编码
     * @param id   数据项ID
     * @return 是否成功
     */
    Boolean deleteDictData(String code, Long id);
}
