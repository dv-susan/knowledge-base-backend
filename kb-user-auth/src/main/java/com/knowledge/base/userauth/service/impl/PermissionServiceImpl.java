package com.knowledge.base.userauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.userauth.dto.PermissionDTO;
import com.knowledge.base.userauth.entity.Permission;
import com.knowledge.base.userauth.mapper.PermissionMapper;
import com.knowledge.base.userauth.service.PermissionService;
import com.knowledge.base.userauth.vo.PermissionVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现权限相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionDTO permissionDTO) {
        log.info("创建权限：permissionName={}", permissionDTO.getName());

        // 检查权限编码是否已存在
        if (StringUtils.hasText(permissionDTO.getCode())) {
            Permission existPermission = permissionMapper.selectOne(
                    new LambdaQueryWrapper<Permission>()
                            .eq(Permission::getPermissionCode, permissionDTO.getCode())
            );
            if (existPermission != null) {
                throw new BusinessException("权限编码已存在");
            }
        }

        // 检查父权限是否存在
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId() > 0) {
            Permission parentPermission = permissionMapper.selectById(permissionDTO.getParentId());
            if (parentPermission == null) {
                throw new BusinessException("父权限不存在");
            }
        }

        // 构建权限实体
        Permission permission = new Permission();
        permission.setId(SnowflakeIdGenerator.getInstance().nextId());
        permission.setParentId(permissionDTO.getParentId() != null ? permissionDTO.getParentId() : 0L);
        permission.setPermissionName(permissionDTO.getName());
        permission.setPermissionCode(permissionDTO.getCode());
        permission.setPermissionType(getPermissionTypeValue(permissionDTO.getType()));
        permission.setMenuUrl(permissionDTO.getMenuUrl());
        permission.setApiUrl(permissionDTO.getApiUrl());
        permission.setMethod(permissionDTO.getMethod());
        permission.setSort(permissionDTO.getSortOrder() != null ? permissionDTO.getSortOrder() : 0);
        permission.setStatus(permissionDTO.getStatus() != null ? permissionDTO.getStatus() : 1);

        // 保存权限
        int count = permissionMapper.insert(permission);
        if (count <= 0) {
            throw new BusinessException("创建权限失败");
        }

        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePermission(PermissionDTO permissionDTO) {
        log.info("更新权限：permissionId={}", permissionDTO.getId());

        if (permissionDTO.getId() == null) {
            throw new BusinessException("权限ID不能为空");
        }

        // 检查权限是否存在
        Permission existPermission = permissionMapper.selectById(permissionDTO.getId());
        if (existPermission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查权限编码是否被其他权限使用
        if (StringUtils.hasText(permissionDTO.getCode())
                && !permissionDTO.getCode().equals(existPermission.getPermissionCode())) {
            Permission permission = permissionMapper.selectOne(
                    new LambdaQueryWrapper<Permission>()
                            .eq(Permission::getPermissionCode, permissionDTO.getCode())
            );
            if (permission != null && !permission.getId().equals(permissionDTO.getId())) {
                throw new BusinessException("权限编码已被使用");
            }
        }

        // 检查父权限是否存在
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId() > 0) {
            if (permissionDTO.getParentId().equals(permissionDTO.getId())) {
                throw new BusinessException("父权限不能是自己");
            }
            Permission parentPermission = permissionMapper.selectById(permissionDTO.getParentId());
            if (parentPermission == null) {
                throw new BusinessException("父权限不存在");
            }
        }

        // 构建更新实体
        Permission permission = new Permission();
        permission.setId(permissionDTO.getId());
        permission.setParentId(permissionDTO.getParentId() != null ? permissionDTO.getParentId() : 0L);
        permission.setPermissionName(permissionDTO.getName());
        permission.setPermissionCode(permissionDTO.getCode());
        if (StringUtils.hasText(permissionDTO.getType())) {
            permission.setPermissionType(getPermissionTypeValue(permissionDTO.getType()));
        }
        permission.setMenuUrl(permissionDTO.getMenuUrl());
        permission.setApiUrl(permissionDTO.getApiUrl());
        permission.setMethod(permissionDTO.getMethod());
        permission.setSort(permissionDTO.getSortOrder());
        permission.setStatus(permissionDTO.getStatus());

        int count = permissionMapper.updateById(permission);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePermission(Long permissionId) {
        log.info("删除权限：permissionId={}", permissionId);

        if (permissionId == null) {
            throw new BusinessException("权限ID不能为空");
        }

        // 检查权限是否存在
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查是否有子权限
        Long childCount = permissionMapper.selectCount(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getParentId, permissionId)
        );
        if (childCount > 0) {
            throw new BusinessException("该权限下有子权限，不能删除");
        }

        // TODO: 检查是否被角色使用，如果有则不允许删除

        // 删除权限
        int count = permissionMapper.deleteById(permissionId);
        return count > 0;
    }

    @Override
    public PermissionVO getPermissionById(Long permissionId) {
        if (permissionId == null) {
            throw new BusinessException("权限ID不能为空");
        }

        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        return convertToVO(permission);
    }

    @Override
    public List<PermissionVO> getPermissionsByParentId(Long parentId) {
        Long targetParentId = parentId != null ? parentId : 0L;
        List<Permission> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getParentId, targetParentId)
                        .orderByAsc(Permission::getSort)
        );

        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<PermissionVO> pagePermissions(Long current, Long size, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Permission::getPermissionName, keyword)
                    .or()
                    .like(Permission::getPermissionCode, keyword);
        }

        // 分页查询
        Page<Permission> page = new Page<>(current, size);
        IPage<Permission> permissionPage = permissionMapper.selectPage(page, wrapper);

        // 转换为VO
        return permissionPage.convert(this::convertToVO);
    }

    @Override
    public List<PermissionVO> getPermissionTree() {
        // 查询所有权限
        List<Permission> allPermissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .orderByAsc(Permission::getSort)
        );

        // 转换为VO
        List<PermissionVO> permissionVOs = allPermissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildPermissionTree(permissionVOs, 0L);
    }

    @Override
    public List<PermissionVO> getAllPermissions() {
        List<Permission> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getStatus, 1)
                        .orderByAsc(Permission::getSort)
        );

        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     *
     * @param permission 权限实体
     * @return 权限VO
     */
    private PermissionVO convertToVO(Permission permission) {
        return PermissionVO.builder()
                .id(permission.getId())
                .name(permission.getPermissionName())
                .code(permission.getPermissionCode())
                .type(getPermissionTypeString(permission.getPermissionType()))
                .parentId(permission.getParentId())
                .menuUrl(permission.getMenuUrl())
                .apiUrl(permission.getApiUrl())
                .method(permission.getMethod())
                .sortOrder(permission.getSort())
                .status(permission.getStatus())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }

    /**
     * 构建权限树
     *
     * @param permissions   权限列表
     * @param parentId 父权限ID
     * @return 权限树
     */
    private List<PermissionVO> buildPermissionTree(List<PermissionVO> permissions, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();

        for (PermissionVO permission : permissions) {
            if (parentId.equals(permission.getParentId())) {
                // 递归查找子权限
                permission.setChildren(buildPermissionTree(permissions, permission.getId()));
                tree.add(permission);
            }
        }

        return tree;
    }

    /**
     * 获取权限类型值
     *
     * @param type 权限类型字符串
     * @return 权限类型值
     */
    private Integer getPermissionTypeValue(String type) {
        if (type == null) {
            return 1;
        }
        return switch (type.trim().toLowerCase()) {
            case "2", "button" -> 2;
            case "3", "api" -> 3;
            case "1", "menu" -> 1;
            default -> 1; // menu
        };
    }

    /**
     * 获取权限类型字符串
     *
     * @param type 权限类型值
     * @return 权限类型字符串
     */
    private String getPermissionTypeString(Integer type) {
        if (type == null) {
            return "menu";
        }
        return switch (type) {
            case 2 -> "button";
            case 3 -> "api";
            default -> "menu";
        };
    }
}
