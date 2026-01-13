package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询所有菜单(树形结构)
     */
    @Select("SELECT * FROM t_menu WHERE is_deleted = 0 ORDER BY sort_order ASC")
    List<Menu> selectAllMenus();

    /**
     * 根据父ID查询子菜单
     */
    @Select("SELECT * FROM t_menu WHERE parent_id = #{parentId} AND is_deleted = 0 ORDER BY sort_order ASC")
    List<Menu> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据角色ID查询菜单列表
     */
    @Select("SELECT DISTINCT m.* FROM t_menu m " +
            "INNER JOIN t_role_menu_relation rmr ON m.id = rmr.menu_id " +
            "WHERE rmr.role_id = #{roleId} AND m.is_deleted = 0 AND m.status = 1 " +
            "ORDER BY m.sort_order ASC")
    List<Menu> selectByRoleId(@Param("roleId") Long roleId);
}
