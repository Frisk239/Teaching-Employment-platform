package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 招聘职位实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_position")
@ApiModel(description = "招聘职位信息")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "职位ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "企业ID")
    private Long companyId;

    @ApiModelProperty(value = "职位名称")
    private String positionName;

    @ApiModelProperty(value = "职位类型：fulltime-全职 parttime-兼职 internship-实习")
    private String positionType;

    @ApiModelProperty(value = "工作城市")
    @TableField("work_city")
    private String city;

    @ApiModelProperty(value = "薪资最低")
    private BigDecimal salaryMin;

    @ApiModelProperty(value = "薪资最高")
    private BigDecimal salaryMax;

    @ApiModelProperty(value = "薪资单位：month-按月 year-按年 day-按天 hour-按时")
    private String salaryUnit;

    @ApiModelProperty(value = "学历要求：junior_college-大专 bachelor-本科 master-硕士 doctor-博士 unlimited-不限")
    @TableField("education_require")
    private String education;

    @ApiModelProperty(value = "工作年限要求")
    @TableField("experience_require")
    private String experience;

    @ApiModelProperty(value = "职位描述")
    private String description;

    @ApiModelProperty(value = "职位要求")
    private String requirements;

    @ApiModelProperty(value = "技术栈（JSON数组）")
    private String techStack;

    @ApiModelProperty(value = "能力雷达图数据（JSON对象）")
    private String capabilityRadar;

    @ApiModelProperty(value = "招聘人数")
    @TableField("recruit_num")
    private Integer recruitCount;

    @ApiModelProperty(value = "已招人数")
    private Integer hiredCount;

    @ApiModelProperty(value = "简历投递数")
    private Integer applicationCount;

    @ApiModelProperty(value = "职位状态：draft-草稿 active-招聘中 paused-暂停 closed-已关闭")
    private String status;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishTime;

    @ApiModelProperty(value = "截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 企业信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Company company;

    /**
     * 企业名称
     */
    @TableField(exist = false)
    private String companyName;
}
