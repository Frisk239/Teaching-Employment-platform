package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.entity.Role;
import com.teaching.employment.service.RoleService;
import com.teaching.employment.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色管理Controller
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation("分页查询角色列表")
    @GetMapping("/page")
    public Result<IPage<Role>> getRolePage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("角色编码") @RequestParam(required = false) String roleCode,
            @ApiParam("角色名称") @RequestParam(required = false) String roleName,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        IPage<Role> page = roleService.getRolePage(current, size, roleCode, roleName, status);
        return Result.ok(page);
    }

    @ApiOperation("查询所有角色(下拉框)")
    @GetMapping("/list")
    public Result<List<Role>> getRoleList(
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        List<Role> list = roleService.getRoleList(status);
        return Result.ok(list);
    }

    @ApiOperation("检查角色编码是否存在")
    @GetMapping("/check-code")
    public Result<Boolean> checkRoleCodeExists(
            @ApiParam("角色编码") @RequestParam String roleCode,
            @ApiParam("角色ID(编辑时传入)") @RequestParam(required = false) Long id) {
        boolean exists = roleService.checkRoleCodeExists(roleCode, id);
        return Result.ok(exists);
    }

    @ApiOperation("根据ID查询角色")
    @GetMapping("/{id}")
    public Result<Role> getRoleById(
            @ApiParam("角色ID") @PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.ok(role);
    }

    @ApiOperation("新增角色")
    @PostMapping
    public Result<String> addRole(@RequestBody Role role) {
        // 检查角色编码是否已存在
        if (roleService.checkRoleCodeExists(role.getRoleCode(), null)) {
            return Result.error("角色编码已存在");
        }
        boolean success = roleService.save(role);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    @ApiOperation("修改角色")
    @PutMapping
    public Result<String> updateRole(@RequestBody Role role) {
        // 检查角色编码是否已存在
        if (roleService.checkRoleCodeExists(role.getRoleCode(), role.getId())) {
            return Result.error("角色编码已存在");
        }
        boolean success = roleService.updateById(role);
        return success ? Result.ok("修改成功") : Result.error("修改失败");
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public Result<String> deleteRole(
            @ApiParam("角色ID") @PathVariable Long id) {
        boolean success = roleService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除角色
     */
    @ApiOperation("批量删除角色")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        // 输入验证
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }

        // 业务逻辑验证：检查角色是否被用户使用
        // TODO: 实现角色使用检查（需要注入 UserService 并添加 countByRoleId 方法）
        // for (Long id : ids) {
        //     long count = userService.countByRoleId(id);
        //     if (count > 0) {
        //         return Result.error("角色ID[" + id + "]正在使用中，无法删除");
        //     }
        // }

        boolean success = roleService.removeByIds(ids);
        return success ? Result.ok("批量删除成功") : Result.error("批量删除失败");
    }

    @ApiOperation("分配菜单权限给角色")
    @PostMapping("/assign-menus")
    public Result<String> assignMenus(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Object> menuIdObjects = (List<Object>) params.get("menuIds");
        List<Long> menuIds = menuIdObjects.stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .collect(Collectors.toList());
        boolean success = roleService.assignMenus(roleId, menuIds);
        return success ? Result.ok("分配成功") : Result.error("分配失败");
    }

    @ApiOperation("分配操作权限给角色")
    @PostMapping("/assign-permissions")
    public Result<String> assignPermissions(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Object> permissionIdObjects = (List<Object>) params.get("permissionIds");
        List<Long> permissionIds = permissionIdObjects.stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .collect(Collectors.toList());
        boolean success = roleService.assignPermissions(roleId, permissionIds);
        return success ? Result.ok("分配成功") : Result.error("分配失败");
    }

    @ApiOperation("获取角色的菜单ID列表")
    @GetMapping("/menus/{roleId}")
    public Result<List<Long>> getRoleMenuIds(
            @ApiParam("角色ID") @PathVariable Long roleId) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return Result.ok(menuIds);
    }

    @ApiOperation("获取角色的权限ID列表")
    @GetMapping("/permissions/{roleId}")
    public Result<List<Long>> getRolePermissionIds(
            @ApiParam("角色ID") @PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissionIds(roleId);
        return Result.ok(permissionIds);
    }
}
