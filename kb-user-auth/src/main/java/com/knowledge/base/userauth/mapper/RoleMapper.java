package com.knowledge.base.userauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.userauth.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 *
 * <p>按照阿里巴巴Java开发规范设计，提供角色数据访问操作</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表，如 ["ROLE_USER", "ROLE_REVIEWER"]
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色编码查询拥有该角色的所有用户ID
     *
     * @param roleCode 角色编码，如 ROLE_REVIEWER
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByRoleCode(@Param("roleCode") String roleCode);

}