package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.RolePermissionRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface RolePermissionRelationMapper extends BaseMapper<RolePermissionRelation> {
}
