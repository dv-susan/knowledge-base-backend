package com.knowledge.base.userauth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.userauth.dto.PermissionDTO;
import com.knowledge.base.userauth.vo.PermissionVO;

import java.util.List;

/**
 * 权限Service接口
 *
 * <p>提供权限相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface PermissionService {

    /**
     * 创建权限
     *
     * @param permissionDTO 权限信息
     * @return 权限ID
     */
    Long createPermission(PermissionDTO permissionDTO);

    /**
     * 更新权限
     *
     * @param permissionDTO 权限信息
     * @return 是否成功
     */
    Boolean updatePermission(PermissionDTO permissionDTO);

    /**
     * 删除权限
     *
     * @param permissionId 权限ID
     * @return 是否成功
     */
    Boolean deletePermission(Long permissionId);

    /**
     * 根据ID查询权限
     *
     * @param permissionId 权限ID
     * @return 权限信息
     */
    PermissionVO getPermissionById(Long permissionId);

    /**
     * 根据父权限ID查询直属子权限
     *
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    List<PermissionVO> getPermissionsByParentId(Long parentId);

    /**
     * 分页查询权限
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @return 权限分页信息
     */
    IPage<PermissionVO> pagePermissions(Long current, Long size, String keyword);

    /**
     * 获取权限树
     *
     * @return 权限树
     */
    List<PermissionVO> getPermissionTree();

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    List<PermissionVO> getAllPermissions();
}
