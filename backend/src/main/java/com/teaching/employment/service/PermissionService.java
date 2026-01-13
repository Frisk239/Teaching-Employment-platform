package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Permission;

import java.util.List;

/**
 * 权限Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 查询所有权限
     */
    List<Permission> getAllPermissions();

    /**
     * 根据角色ID查询权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);

    /**
     * 根据权限类型查询权限列表
     */
    List<Permission> getPermissionsByType(String permissionType);

    /**
     * 构建权限树
     */
    List<Permission> buildPermissionTree(List<Permission> permissions);

    /**
     * 为角色分配权限
     */
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * 移除角色权限关联
     */
    boolean removePermissionsFromRole(Long roleId, List<Long> permissionIds);
}
