package com.knowledge.base.userauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.userauth.dto.RoleDTO;
import com.knowledge.base.userauth.entity.Role;
import com.knowledge.base.userauth.mapper.RoleMapper;
import com.knowledge.base.userauth.service.RoleService;
import com.knowledge.base.userauth.vo.RoleVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现角色相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleDTO roleDTO) {
        log.info("创建角色：roleName={}", roleDTO.getName());

        // 检查角色名称是否已存在
        Role existRole = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getRoleName, roleDTO.getName())
        );
        if (existRole != null) {
            throw new BusinessException("角色名称已存在");
        }

        // 检查角色编码是否已存在
        if (StringUtils.hasText(roleDTO.getCode())) {
            existRole = roleMapper.selectOne(
                    new LambdaQueryWrapper<Role>()
                            .eq(Role::getRoleCode, roleDTO.getCode())
            );
            if (existRole != null) {
                throw new BusinessException("角色编码已存在");
            }
        }

        // 构建角色实体
        Role role = new Role();
        role.setId(SnowflakeIdGenerator.getInstance().nextId());
        role.setRoleName(roleDTO.getName());
        role.setRoleCode(roleDTO.getCode());
        role.setDescription(roleDTO.getDescription());
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);
        role.setSort(0);

        // 保存角色
        int count = roleMapper.insert(role);
        if (count <= 0) {
            throw new BusinessException("创建角色失败");
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRole(RoleDTO roleDTO) {
        log.info("更新角色：roleId={}", roleDTO.getId());

        if (roleDTO.getId() == null) {
            throw new BusinessException("角色ID不能为空");
        }

        // 检查角色是否存在
        Role existRole = roleMapper.selectById(roleDTO.getId());
        if (existRole == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色名称是否被其他角色使用
        if (StringUtils.hasText(roleDTO.getName())
                && !roleDTO.getName().equals(existRole.getRoleName())) {
            Role role = roleMapper.selectOne(
                    new LambdaQueryWrapper<Role>()
                            .eq(Role::getRoleName, roleDTO.getName())
            );
            if (role != null && !role.getId().equals(roleDTO.getId())) {
                throw new BusinessException("角色名称已被使用");
            }
        }

        // 检查角色编码是否被其他角色使用
        if (StringUtils.hasText(roleDTO.getCode())
                && !roleDTO.getCode().equals(existRole.getRoleCode())) {
            Role role = roleMapper.selectOne(
                    new LambdaQueryWrapper<Role>()
                            .eq(Role::getRoleCode, roleDTO.getCode())
            );
            if (role != null && !role.getId().equals(roleDTO.getId())) {
                throw new BusinessException("角色编码已被使用");
            }
        }

        // 构建更新实体
        Role role = new Role();
        role.setId(roleDTO.getId());
        role.setRoleName(roleDTO.getName());
        role.setRoleCode(roleDTO.getCode());
        role.setDescription(roleDTO.getDescription());
        role.setStatus(roleDTO.getStatus());

        int count = roleMapper.updateById(role);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRole(Long roleId) {
        log.info("删除角色：roleId={}", roleId);

        if (roleId == null) {
            throw new BusinessException("角色ID不能为空");
        }

        // 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        long assignedUserCount = countAssignedUsers(roleId);
        if (assignedUserCount > 0) {
            throw new BusinessException("当前角色已分配给用户，无法删除");
        }

        // 删除角色权限关联
        jdbcTemplate.update("DELETE FROM kb_role_permission WHERE role_id = ?", roleId);

        // 顺手清理可能残留的历史角色关联，避免脏数据继续存在
        jdbcTemplate.update("DELETE FROM kb_user_role WHERE role_id = ?", roleId);

        // 删除角色
        int count = roleMapper.deleteById(roleId);
        return count > 0;
    }

    @Override
    public RoleVO getRoleById(Long roleId) {
        if (roleId == null) {
            throw new BusinessException("角色ID不能为空");
        }

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        return buildRoleVO(role, true);
    }

    @Override
    public IPage<RoleVO> pageRoles(Long current, Long size, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Role::getRoleName, keyword)
                    .or()
                    .like(Role::getRoleCode, keyword);
        }

        // 分页查询
        Page<Role> page = new Page<>(current, size);
        IPage<Role> rolePage = roleMapper.selectPage(page, wrapper);

        // 转换为VO
        return rolePage.convert(role -> buildRoleVO(role, true));
    }

    @Override
    public List<RoleVO> getAllRoles() {
        List<Role> roles = roleMapper.selectList(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getStatus, 1)
                        .orderByAsc(Role::getSort)
        );

        return roles.stream()
                .map(role -> buildRoleVO(role, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        log.info("分配权限：roleId={}, permissionCount={}", roleId, permissionIds.size());

        if (roleId == null) {
            throw new BusinessException("角色ID不能为空");
        }

        // 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 删除原有权限关联
        jdbcTemplate.update("DELETE FROM kb_role_permission WHERE role_id = ?", roleId);

        // 批量插入权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            String sql = "INSERT INTO kb_role_permission (id, role_id, permission_id, created_at) VALUES (?, ?, ?, NOW())";
            for (Long permissionId : permissionIds) {
                jdbcTemplate.update(sql, SnowflakeIdGenerator.getInstance().nextId(), roleId, permissionId);
            }
        }

        return true;
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        if (roleId == null) {
            throw new BusinessException("角色ID不能为空");
        }

        return jdbcTemplate.queryForList(
                "SELECT permission_id FROM kb_role_permission WHERE role_id = ?",
                Long.class,
                roleId
        );
    }

    private RoleVO buildRoleVO(Role role, boolean includePermissions) {
        List<String> permissions = includePermissions
                ? getRolePermissions(role.getId()).stream().map(String::valueOf).collect(Collectors.toList())
                : List.of();
        Long userCount = countAssignedUsers(role.getId());
        return RoleVO.builder()
                .id(role.getId())
                .name(role.getRoleName())
                .code(role.getRoleCode())
                .description(role.getDescription())
                .status(role.getStatus())
                .permissions(permissions)
                .userCount(userCount != null ? userCount : 0L)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    private Long countAssignedUsers(Long roleId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT ur.user_id) " +
                        "FROM kb_user_role ur " +
                        "INNER JOIN kb_user u ON ur.user_id = u.id " +
                        "WHERE ur.role_id = ? AND u.deleted = 0",
                Long.class,
                roleId
        );
    }
}
