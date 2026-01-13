package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 权限实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_permission")
@ApiModel(description = "权限信息")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "权限ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "父权限ID，0为根权限")
    private Long parentId;

    @ApiModelProperty(value = "权限名称")
    private String permissionName;

    @ApiModelProperty(value = "权限编码")
    private String permissionCode;

    @ApiModelProperty(value = "权限类型：menu-菜单权限 button-按钮权限 api-接口权限")
    private String permissionType;

    @ApiModelProperty(value = "权限路径")
    private String path;

    @ApiModelProperty(value = "排序")
    private Integer sortOrder;

    @ApiModelProperty(value = "状态：1-正常 0-停用")
    private Integer status;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    /**
     * 子权限列表（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private List<Permission> children;
}
