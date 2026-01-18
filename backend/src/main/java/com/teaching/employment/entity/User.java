package com.teaching.employment.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_user")
@ApiModel(description = "用户信息")
@ContentRowHeight(20)
@HeadRowHeight(30)
@ColumnWidth(20)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty(value = "用户ID", index = 0)
    private Long id;

    @ApiModelProperty(value = "用户名")
    @ExcelProperty(value = "用户名", index = 1)
    private String username;

    @ApiModelProperty(value = "密码（加密）")
    private String password;

    @ApiModelProperty(value = "真实姓名")
    @ExcelProperty(value = "姓名", index = 2)
    private String realName;

    @ApiModelProperty(value = "手机号")
    @ExcelProperty(value = "手机号", index = 3)
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @ExcelProperty(value = "邮箱", index = 4)
    private String email;

    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "所属学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "所属企业ID（企业对接人专用）")
    private Long companyId;

    @ApiModelProperty(value = "学号（学员专用）")
    private String studentNumber;

    @ApiModelProperty(value = "简历文件URL（学员专用）")
    private String resumeUrl;

    @ApiModelProperty(value = "状态：1-正常 0-停用")
    @ExcelProperty(value = "状态", index = 5)
    private Integer status;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @ExcelProperty(value = "创建时间", index = 6)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 角色信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Role role;

    /**
     * 学校信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private School school;

    /**
     * 角色编码（关联查询时使用）
     */
    @TableField(exist = false)
    @ExcelProperty(value = "角色", index = 7)
    private String roleCode;
}
