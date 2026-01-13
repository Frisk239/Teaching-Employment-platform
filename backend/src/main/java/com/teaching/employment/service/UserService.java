package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.User;

/**
 * 用户Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param roleId   角色ID（可选）
     * @param schoolId 学校ID（可选）
     * @param keyword  关键词（可选）
     * @return 用户分页列表
     */
    IPage<User> getUserPage(Integer current, Integer size, Long roleId, Long schoolId, String keyword);

    /**
     * 创建用户（包含密码加密）
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean createUser(User user);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}
