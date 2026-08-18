package com.knowledge.base.userauth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.userauth.dto.RoleDTO;
import com.knowledge.base.userauth.vo.RoleVO;

import java.util.List;

/**
 * 角色Service接口
 *
 * <p>提供角色相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param roleDTO 角色信息
     * @return 角色ID
     */
    Long createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     *
     * @param roleDTO 角色信息
     * @return 是否成功
     */
    Boolean updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    Boolean deleteRole(Long roleId);

    /**
     * 根据ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    RoleVO getRoleById(Long roleId);

    /**
     * 分页查询角色
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @return 角色分页信息
     */
    IPage<RoleVO> pageRoles(Long current, Long size, String keyword);

    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    List<RoleVO> getAllRoles();

    /**
     * 分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    Boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissions(Long roleId);
}
