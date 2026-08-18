package com.knowledge.base.foundation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.foundation.entity.Dict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 字典类型Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    @Select("SELECT * FROM kb_dict WHERE dict_code = #{dictCode}")
    Dict selectByDictCode(@Param("dictCode") String dictCode);

    @Select("SELECT * FROM kb_dict WHERE dict_type = #{dictType} AND status = 1 ORDER BY sort")
    List<Dict> selectByDictType(@Param("dictType") String dictType);
}