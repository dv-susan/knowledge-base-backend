package com.knowledge.base.foundation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.foundation.entity.DictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 字典数据Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {

    @Select("SELECT * FROM kb_dict_data WHERE dict_id = #{dictId} AND status = 1 ORDER BY dict_sort")
    List<DictData> selectByDictId(@Param("dictId") Long dictId);

    @Select("SELECT * FROM kb_dict_data WHERE dict_code = #{dictCode} AND status = 1 ORDER BY dict_sort")
    List<DictData> selectByDictCode(@Param("dictCode") String dictCode);
}