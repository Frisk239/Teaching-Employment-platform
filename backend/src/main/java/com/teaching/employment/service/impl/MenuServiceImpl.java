package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Menu;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.MenuMapper;
import com.teaching.employment.mapper.RoleMenuRelationMapper;
import com.teaching.employment.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final MenuMapper menuMapper;
    private final RoleMenuRelationMapper roleMenuRelationMapper;

    @Override
    public List<Menu> getAllMenus() {
        return menuMapper.selectAllMenus();
    }

    @Override
    public List<Menu> getMenusByParentId(Long parentId) {
        return menuMapper.selectByParentId(parentId);
    }

    @Override
    public List<Menu> getMenusByRoleId(Long roleId) {
        return menuMapper.selectByRoleId(roleId);
    }

    @Override
    public List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> tree = new ArrayList<>();
        List<Menu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .collect(Collectors.toList());

        for (Menu rootMenu : rootMenus) {
            buildChildren(rootMenu, menus);
            tree.add(rootMenu);
        }

        return tree;
    }

    private void buildChildren(Menu parent, List<Menu> allMenus) {
        List<Menu> children = allMenus.stream()
                .filter(menu -> parent.getId().equals(menu.getParentId()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (Menu child : children) {
                buildChildren(child, allMenus);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignMenusToRole(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            throw new BusinessException("菜单ID列表不能为空");
        }

        // 先删除已有的角色菜单关联
        LambdaQueryWrapper<com.teaching.employment.entity.RoleMenuRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.teaching.employment.entity.RoleMenuRelation::getRoleId, roleId);
        roleMenuRelationMapper.delete(wrapper);

        // 添加新的关联
        for (Long menuId : menuIds) {
            com.teaching.employment.entity.RoleMenuRelation relation = new com.teaching.employment.entity.RoleMenuRelation();
            relation.setRoleId(roleId);
            relation.setMenuId(menuId);
            roleMenuRelationMapper.insert(relation);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMenusFromRole(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return true;
        }

        for (Long menuId : menuIds) {
            LambdaQueryWrapper<com.teaching.employment.entity.RoleMenuRelation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(com.teaching.employment.entity.RoleMenuRelation::getRoleId, roleId)
                    .eq(com.teaching.employment.entity.RoleMenuRelation::getMenuId, menuId);
            roleMenuRelationMapper.delete(wrapper);
        }

        return true;
    }
}
