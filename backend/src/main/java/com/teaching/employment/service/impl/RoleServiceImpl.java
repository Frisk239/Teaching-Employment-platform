package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Role;
import com.teaching.employment.entity.RoleMenuRelation;
import com.teaching.employment.entity.RolePermissionRelation;
import com.teaching.employment.entity.User;
import com.teaching.employment.mapper.RoleMapper;
import com.teaching.employment.mapper.UserMapper;
import com.teaching.employment.service.RoleMenuRelationService;
import com.teaching.employment.service.RolePermissionRelationService;
import com.teaching.employment.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuRelationService roleMenuRelationService;

    @Autowired
    private RolePermissionRelationService rolePermissionRelationService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<Role> getRolePage(Integer current, Integer size, String roleCode, String roleName, Integer status) {
        Page<Role> page = new Page<>(current, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(roleCode)) {
            wrapper.like(Role::getRoleCode, roleCode);
        }
        if (StringUtils.hasText(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }
        wrapper.orderByDesc(Role::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<Role> getRoleList(Integer status) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }
        wrapper.orderByAsc(Role::getId);
        return this.list(wrapper);
    }

    @Override
    public boolean checkRoleCodeExists(String roleCode, Long id) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        if (id != null) {
            wrapper.ne(Role::getId, id);
        }
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有的角色菜单关系
        LambdaQueryWrapper<RoleMenuRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenuRelation::getRoleId, roleId);
        roleMenuRelationService.remove(wrapper);

        // 如果menuIds为空或null,直接返回
        if (menuIds == null || menuIds.isEmpty()) {
            return true;
        }

        // 批量插入新的角色菜单关系
        List<RoleMenuRelation> relations = menuIds.stream()
                .map(menuId -> {
                    RoleMenuRelation relation = new RoleMenuRelation();
                    relation.setRoleId(roleId);
                    relation.setMenuId(menuId);
                    return relation;
                })
                .collect(Collectors.toList());

        return roleMenuRelationService.saveBatch(relations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除原有的角色权限关系
        LambdaQueryWrapper<RolePermissionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionRelation::getRoleId, roleId);
        rolePermissionRelationService.remove(wrapper);

        // 如果permissionIds为空或null,直接返回
        if (permissionIds == null || permissionIds.isEmpty()) {
            return true;
        }

        // 批量插入新的角色权限关系
        List<RolePermissionRelation> relations = permissionIds.stream()
                .map(permissionId -> {
                    RolePermissionRelation relation = new RolePermissionRelation();
                    relation.setRoleId(roleId);
                    relation.setPermissionId(permissionId);
                    return relation;
                })
                .collect(Collectors.toList());

        return rolePermissionRelationService.saveBatch(relations);
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        LambdaQueryWrapper<RoleMenuRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenuRelation::getRoleId, roleId);
        wrapper.select(RoleMenuRelation::getMenuId);

        return roleMenuRelationService.list(wrapper)
                .stream()
                .map(RoleMenuRelation::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        LambdaQueryWrapper<RolePermissionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionRelation::getRoleId, roleId);
        wrapper.select(RolePermissionRelation::getPermissionId);

        return rolePermissionRelationService.list(wrapper)
                .stream()
                .map(RolePermissionRelation::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersByRoleId(Long roleId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRoleId, roleId);
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectList(wrapper);
    }
}
