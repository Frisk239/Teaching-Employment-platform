package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 查询所有权限
     */
    @Select("SELECT * FROM t_permission WHERE is_deleted = 0 ORDER BY sort_order ASC")
    List<Permission> selectAllPermissions();

    /**
     * 根据角色ID查询权限列表
     */
    @Select("SELECT DISTINCT p.* FROM t_permission p " +
            "INNER JOIN t_role_permission_relation rpr ON p.id = rpr.permission_id " +
            "WHERE rpr.role_id = #{roleId} AND p.is_deleted = 0 AND p.status = 1")
    List<Permission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限类型查询权限列表
     */
    @Select("SELECT * FROM t_permission WHERE permission_type = #{permissionType} AND is_deleted = 0")
    List<Permission> selectByType(@Param("permissionType") String permissionType);
}
