package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户（包含角色和学校信息）
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT u.*, r.role_code, r.role_name, s.school_name " +
            "FROM t_user u " +
            "LEFT JOIN t_role r ON u.role_id = r.id " +
            "LEFT JOIN t_school s ON u.school_id = s.id " +
            "WHERE u.username = #{username} AND u.is_deleted = 0")
    User selectUserWithDetailsByUsername(@Param("username") String username);
}
