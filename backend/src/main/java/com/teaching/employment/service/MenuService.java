package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Menu;

import java.util.List;

/**
 * 菜单Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface MenuService extends IService<Menu> {

    /**
     * 查询所有菜单(树形结构)
     */
    List<Menu> getAllMenus();

    /**
     * 根据父ID查询子菜单
     */
    List<Menu> getMenusByParentId(Long parentId);

    /**
     * 根据角色ID查询菜单列表
     */
    List<Menu> getMenusByRoleId(Long roleId);

    /**
     * 构建菜单树
     */
    List<Menu> buildMenuTree(List<Menu> menus);

    /**
     * 为角色分配菜单
     */
    boolean assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 移除角色菜单关联
     */
    boolean removeMenusFromRole(Long roleId, List<Long> menuIds);
}
