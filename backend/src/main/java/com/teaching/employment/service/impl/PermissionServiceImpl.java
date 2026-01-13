package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Permission;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.PermissionMapper;
import com.teaching.employment.mapper.RolePermissionRelationMapper;
import com.teaching.employment.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionRelationMapper rolePermissionRelationMapper;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectAllPermissions();
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }

    @Override
    public List<Permission> getPermissionsByType(String permissionType) {
        return permissionMapper.selectByType(permissionType);
    }

    @Override
    public List<Permission> buildPermissionTree(List<Permission> permissions) {
        List<Permission> tree = new ArrayList<>();
        List<Permission> rootPermissions = permissions.stream()
                .filter(permission -> permission.getParentId() == null || permission.getParentId() == 0)
                .collect(Collectors.toList());

        for (Permission rootPermission : rootPermissions) {
            buildChildren(rootPermission, permissions);
            tree.add(rootPermission);
        }

        return tree;
    }

    private void buildChildren(Permission parent, List<Permission> allPermissions) {
        List<Permission> children = allPermissions.stream()
                .filter(permission -> parent.getId().equals(permission.getParentId()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (Permission child : children) {
                buildChildren(child, allPermissions);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new BusinessException("权限ID列表不能为空");
        }

        // 先删除已有的角色权限关联
        LambdaQueryWrapper<com.teaching.employment.entity.RolePermissionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.teaching.employment.entity.RolePermissionRelation::getRoleId, roleId);
        rolePermissionRelationMapper.delete(wrapper);

        // 添加新的关联
        for (Long permissionId : permissionIds) {
            com.teaching.employment.entity.RolePermissionRelation relation = new com.teaching.employment.entity.RolePermissionRelation();
            relation.setRoleId(roleId);
            relation.setPermissionId(permissionId);
            rolePermissionRelationMapper.insert(relation);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return true;
        }

        for (Long permissionId : permissionIds) {
            LambdaQueryWrapper<com.teaching.employment.entity.RolePermissionRelation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(com.teaching.employment.entity.RolePermissionRelation::getRoleId, roleId)
                    .eq(com.teaching.employment.entity.RolePermissionRelation::getPermissionId, permissionId);
            rolePermissionRelationMapper.delete(wrapper);
        }

        return true;
    }
}
