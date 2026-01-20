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
 * 学员实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_student")
@ApiModel(description = "学员信息")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学员ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "学号")
    private String studentNo;

    @ApiModelProperty(value = "所属学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "班级")
    private String className;

    @ApiModelProperty(value = "专业")
    private String major;

    @ApiModelProperty(value = "年级")
    private String grade;

    @ApiModelProperty(value = "性别：1-男 2-女")
    private Integer gender;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "家庭地址")
    private String address;

    @ApiModelProperty(value = "入学日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate;

    @ApiModelProperty(value = "预计毕业日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    @ApiModelProperty(value = "政治面貌")
    private String politicalStatus;

    @ApiModelProperty(value = "民族")
    private String nation;

    @ApiModelProperty(value = "监护人姓名")
    private String guardianName;

    @ApiModelProperty(value = "监护人电话")
    private String guardianPhone;

    @ApiModelProperty(value = "备注信息")
    private String description;

    @ApiModelProperty(value = "QQ号码")
    private String qq;

    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "健康状况")
    private String healthStatus;

    @ApiModelProperty(value = "个人简介")
    private String bio;

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
     * 学生姓名（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String name;

    /**
     * 学校名称（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String schoolName;

    /**
     * 真实姓名（来自用户表）
     */
    @TableField(exist = false)
    private String realName;

    /**
     * 简历URL（来自用户表，关联查询时使用）
     */
    @TableField(exist = false)
    private String resumeUrl;

    /**
     * 就业状态（统计数据，不映射到数据库字段）
     * employed: 已就业, seeking: 求职中, admitted: 已录取, further: 继续深造
     */
    @TableField(exist = false)
    private String employmentStatus;

    /**
     * 求职申请数量（统计数据，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Integer applicationCount;

    /**
     * 笔试数量（统计数据，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Integer writtenTestCount;

    /**
     * 面试数量（统计数据，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Integer interviewCount;

    /**
     * Offer数量（统计数据，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Integer offerCount;
}
