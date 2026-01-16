package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.entity.User;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.TeacherService;
import com.teaching.employment.service.UserService;
import com.teaching.employment.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Api(tags = "认证管理")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final StudentService studentService;
    private final TeacherService teacherService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        log.info("用户登录: username={}", username);

        try {
            User user = userService.login(username, password);

            // 生成JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            data.put("roleCode", user.getRoleCode());

            // 如果是学员角色,查询并返回studentId
            if (user.getRoleId() == 4) {
                Student student = studentService.getOne(
                    new LambdaQueryWrapper<Student>().eq(Student::getUserId, user.getId())
                );
                if (student != null) {
                    data.put("studentId", student.getId());
                }
            }

            // 如果是教师角色,查询并返回teacherId
            if (user.getRoleId() == 3) {
                Teacher teacher = teacherService.getOne(
                    new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getId())
                );
                if (teacher != null) {
                    data.put("teacherId", teacher.getId());
                }
            }

            return Result.ok("登录成功", data);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<Void> register(@RequestBody User user) {
        log.info("用户注册: username={}", user.getUsername());

        try {
            boolean success = userService.createUser(user);
            if (success) {
                return Result.ok("注册成功");
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    @ApiOperation("获取当前用户信息")
    public Result<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // 去掉Bearer前缀
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(actualToken);

            // 使用关联查询获取用户信息（包含角色和学校）
            User user = userService.getUserWithDetailsById(userId);

            if (user == null) {
                return Result.error("用户不存在");
            }

            // 填充roleCode字段(与login保持一致)
            String roleCode = null;
            if (user.getRole() != null) {
                roleCode = user.getRole().getRoleCode();
                user.setRoleCode(roleCode);
            }

            // 构造返回数据，与login接口保持一致
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("roleCode", roleCode);

            // 如果是学员角色,查询并返回studentId
            if (user.getRoleId() == 4) {
                Student student = studentService.getOne(
                    new LambdaQueryWrapper<Student>().eq(Student::getUserId, user.getId())
                );
                if (student != null) {
                    data.put("studentId", student.getId());
                }
            }

            // 如果是教师角色,查询并返回teacherId
            if (user.getRoleId() == 3) {
                Teacher teacher = teacherService.getOne(
                    new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getId())
                );
                if (teacher != null) {
                    data.put("teacherId", teacher.getId());
                }
            }

            return Result.ok(data);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public Result<Void> logout() {
        return Result.ok("退出成功");
    }
}
