package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 指导记录实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Data
@TableName("t_guidance_record")
@ApiModel(description = "指导记录")
public class GuidanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学员ID")
    private Long studentId;

    @ApiModelProperty(value = "教师ID")
    private Long teacherId;

    @ApiModelProperty(value = "指导类型：career_planning-职业规划，resume_guidance-简历指导，interview_guidance-面试指导，skill_improvement-技能提升，psychological_counseling-心理辅导，other-其他")
    private String guidanceType;

    @ApiModelProperty(value = "指导内容")
    private String content;

    @ApiModelProperty(value = "后续跟进计划")
    private String nextAction;

    @ApiModelProperty(value = "指导时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime guidanceDate;

    @ApiModelProperty(value = "指导地点（线上/线下）")
    private String location;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 学员信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 教师信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Teacher teacher;

    /**
     * 学员姓名（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 教师姓名（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 指导类型名称（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String guidanceTypeName;
}
