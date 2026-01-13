package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Permission;
import com.teaching.employment.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/permission")
@Api(tags = "权限管理")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 查询所有权限(树形结构)
     */
    @GetMapping("/tree")
    @ApiOperation("查询所有权限树")
    public Result<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        List<Permission> tree = permissionService.buildPermissionTree(permissions);
        return Result.ok(tree);
    }

    /**
     * 根据角色ID查询权限列表
     */
    @GetMapping("/role/{roleId}")
    @ApiOperation("根据角色ID查询权限列表")
    public Result<List<Permission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.ok(permissions);
    }

    /**
     * 根据权限类型查询权限列表
     */
    @GetMapping("/type/{permissionType}")
    @ApiOperation("根据权限类型查询权限列表")
    public Result<List<Permission>> getPermissionsByType(@PathVariable String permissionType) {
        List<Permission> permissions = permissionService.getPermissionsByType(permissionType);
        return Result.ok(permissions);
    }

    /**
     * 根据ID查询权限
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询权限")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.ok(permission);
    }

    /**
     * 新增权限
     */
    @PostMapping
    @ApiOperation("新增权限")
    public Result<Void> createPermission(@RequestBody Permission permission) {
        boolean success = permissionService.save(permission);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新权限
     */
    @PutMapping
    @ApiOperation("更新权限")
    public Result<Void> updatePermission(@RequestBody Permission permission) {
        boolean success = permissionService.updateById(permission);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除权限")
    public Result<Void> deletePermission(@PathVariable Long id) {
        boolean success = permissionService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/assign")
    @ApiOperation("为角色分配权限")
    public Result<Void> assignPermissionsToRole(@RequestBody Map<String, Object> request) {
        Long roleId = Long.valueOf(request.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) request.get("permissionIds");
        boolean success = permissionService.assignPermissionsToRole(roleId, permissionIds);
        return success ? Result.ok("分配成功") : Result.error("分配失败");
    }

    /**
     * 移除角色权限关联
     */
    @PostMapping("/remove")
    @ApiOperation("移除角色权限关联")
    public Result<Void> removePermissionsFromRole(@RequestBody Map<String, Object> request) {
        Long roleId = Long.valueOf(request.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) request.get("permissionIds");
        boolean success = permissionService.removePermissionsFromRole(roleId, permissionIds);
        return success ? Result.ok("移除成功") : Result.error("移除失败");
    }
}
