package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.User;
import com.teaching.employment.entity.Role;
import com.teaching.employment.service.UserService;
import com.teaching.employment.service.RoleService;
import com.teaching.employment.utils.ExcelUtil;
import com.teaching.employment.utils.PasswordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private final RoleService roleService;

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
     * 更新个人资料
     */
    @PutMapping("/profile")
    @ApiOperation("更新个人资料")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody UpdateProfileRequest req) {
        // 从request attribute中获取当前用户ID(JwtInterceptor已设置)
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 只允许更新特定字段
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());

        boolean success = userService.updateById(user);
        return success ? Result.ok("保存成功") : Result.error("保存失败");
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

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @ApiOperation("修改密码")
    public Result<Void> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest req) {
        // 从request attribute中获取当前用户ID(JwtInterceptor已设置)
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        boolean success = userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
        return success ? Result.ok("密码修改成功") : Result.error("密码修改失败");
    }

    /**
     * 修改密码请求体
     */
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    /**
     * 更新个人资料请求体
     */
    public static class UpdateProfileRequest {
        private String realName;
        private String phone;
        private String email;

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除用户")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        boolean success = userService.removeByIds(ids);
        return success ? Result.ok("批量删除成功") : Result.error("批量删除失败");
    }

    /**
     * 导出用户
     */
    @GetMapping("/export")
    @ApiOperation("导出用户")
    public void exportUsers(HttpServletResponse response) throws IOException {
        List<User> users = userService.list();
        System.out.println("========== 导出用户数量: " + users.size() + " ==========");

        // 填充roleCode字段
        for (User user : users) {
            if (user.getRoleId() != null) {
                Role role = roleService.getById(user.getRoleId());
                if (role != null) {
                    user.setRoleCode(role.getRoleCode());
                }
            }
            System.out.println("用户: " + user.getUsername() + ", 角色: " + user.getRoleCode());
        }

        ExcelUtil.export(response, "用户数据", users, User.class);
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/password")
    @ApiOperation("重置密码")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String newPassword = params.get("newPassword");
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(PasswordUtil.encrypt(newPassword));
        boolean success = userService.updateById(user);
        return success ? Result.ok("密码重置成功") : Result.error("密码重置失败");
    }
}
