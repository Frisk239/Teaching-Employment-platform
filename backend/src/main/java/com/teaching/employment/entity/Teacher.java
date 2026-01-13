package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教师实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_teacher")
@ApiModel(description = "教师信息")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教师ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "教师工号")
    private String teacherNo;

    @ApiModelProperty(value = "所属学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "所属院系")
    private String department;

    @ApiModelProperty(value = "职称")
    private String title;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "专业领域")
    private String specialty;

    @ApiModelProperty(value = "入职日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "性别：1-男 2-女")
    private Integer gender;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @ApiModelProperty(value = "家庭地址")
    private String address;

    @ApiModelProperty(value = "个人简介")
    private String description;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 用户信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private User user;

    /**
     * 学校信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private School school;

    /**
     * 真实姓名（来自用户表）
     */
    @TableField(exist = false)
    private String realName;
}
