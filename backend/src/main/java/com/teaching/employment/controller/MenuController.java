package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Menu;
import com.teaching.employment.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/menu")
@Api(tags = "菜单管理")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 查询所有菜单(树形结构)
     */
    @GetMapping("/tree")
    @ApiOperation("查询所有菜单树")
    public Result<List<Menu>> getAllMenus() {
        List<Menu> menus = menuService.getAllMenus();
        List<Menu> tree = menuService.buildMenuTree(menus);
        return Result.ok(tree);
    }

    /**
     * 根据角色ID查询菜单列表
     */
    @GetMapping("/role/{roleId}")
    @ApiOperation("根据角色ID查询菜单列表")
    public Result<List<Menu>> getMenusByRoleId(@PathVariable Long roleId) {
        List<Menu> menus = menuService.getMenusByRoleId(roleId);
        List<Menu> tree = menuService.buildMenuTree(menus);
        return Result.ok(tree);
    }

    /**
     * 根据父ID查询子菜单
     */
    @GetMapping("/parent/{parentId}")
    @ApiOperation("根据父ID查询子菜单")
    public Result<List<Menu>> getMenusByParentId(@PathVariable Long parentId) {
        List<Menu> menus = menuService.getMenusByParentId(parentId);
        return Result.ok(menus);
    }

    /**
     * 根据ID查询菜单
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询菜单")
    public Result<Menu> getMenuById(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return Result.ok(menu);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @ApiOperation("新增菜单")
    public Result<Void> createMenu(@RequestBody Menu menu) {
        boolean success = menuService.save(menu);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新菜单
     */
    @PutMapping
    @ApiOperation("更新菜单")
    public Result<Void> updateMenu(@RequestBody Menu menu) {
        boolean success = menuService.updateById(menu);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除菜单")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        boolean success = menuService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除菜单
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除菜单")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        // 输入验证
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }

        // 业务逻辑验证：检查菜单是否存在子菜单
        for (Long id : ids) {
            long childCount = menuService.count(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, id));
            if (childCount > 0) {
                return Result.error("菜单ID[" + id + "]存在子菜单，请先删除子菜单");
            }
        }

        boolean success = menuService.removeByIds(ids);
        return success ? Result.ok("批量删除成功") : Result.error("批量删除失败");
    }

    /**
     * 为角色分配菜单
     */
    @PostMapping("/assign")
    @ApiOperation("为角色分配菜单")
    public Result<Void> assignMenusToRole(@RequestBody Map<String, Object> request) {
        Long roleId = Long.valueOf(request.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> menuIds = (List<Long>) request.get("menuIds");
        boolean success = menuService.assignMenusToRole(roleId, menuIds);
        return success ? Result.ok("分配成功") : Result.error("分配失败");
    }

    /**
     * 移除角色菜单关联
     */
    @PostMapping("/remove")
    @ApiOperation("移除角色菜单关联")
    public Result<Void> removeMenusFromRole(@RequestBody Map<String, Object> request) {
        Long roleId = Long.valueOf(request.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> menuIds = (List<Long>) request.get("menuIds");
        boolean success = menuService.removeMenusFromRole(roleId, menuIds);
        return success ? Result.ok("移除成功") : Result.error("移除失败");
    }
}
