package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param roleCode 角色编码
     * @param roleName 角色名称
     * @param status 状态
     * @return 分页结果
     */
    IPage<Role> getRolePage(Integer current, Integer size, String roleCode, String roleName, Integer status);

    /**
     * 查询所有角色(用于下拉框)
     *
     * @param status 状态
     * @return 角色列表
     */
    List<Role> getRoleList(Integer status);

    /**
     * 检查角色编码是否存在
     *
     * @param roleCode 角色编码
     * @param id 角色ID(编辑时传入)
     * @return 是否存在
     */
    boolean checkRoleCodeExists(String roleCode, Long id);

    /**
     * 分配菜单权限给角色
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 分配操作权限给角色
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}
