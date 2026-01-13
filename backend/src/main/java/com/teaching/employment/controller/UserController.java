package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.User;
import com.teaching.employment.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询用户列表")
    public Result<IPage<User>> getUserPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("角色ID") @RequestParam(required = false) Long roleId,
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<User> page = userService.getUserPage(current, size, roleId, schoolId, keyword);
        return Result.ok(page);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询用户")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.ok(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @ApiOperation("新增用户")
    public Result<Void> createUser(@RequestBody User user) {
        boolean success = userService.createUser(user);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新用户
     */
    @PutMapping
    @ApiOperation("更新用户")
    public Result<Void> updateUser(@RequestBody User user) {
        boolean success = userService.updateById(user);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }
}
