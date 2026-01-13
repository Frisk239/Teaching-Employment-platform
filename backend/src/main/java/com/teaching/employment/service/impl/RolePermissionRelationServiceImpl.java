package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.RolePermissionRelation;
import com.teaching.employment.mapper.RolePermissionRelationMapper;
import com.teaching.employment.service.RolePermissionRelationService;
import org.springframework.stereotype.Service;

/**
 * 角色权限关系服务实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
public class RolePermissionRelationServiceImpl extends ServiceImpl<RolePermissionRelationMapper, RolePermissionRelation> implements RolePermissionRelationService {
}
