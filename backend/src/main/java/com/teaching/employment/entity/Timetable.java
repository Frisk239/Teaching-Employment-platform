package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 课程表实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-16
 */
@Data
@TableName("t_timetable")
@ApiModel(description = "课程表信息")
public class Timetable implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @ApiModelProperty(value = "星期几（1-7，1=周一）")
    private Integer dayOfWeek;

    @ApiModelProperty(value = "节次（1-12）")
    private Integer period;

    @ApiModelProperty(value = "教室ID")
    private Long classroomId;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @ApiModelProperty(value = "学期（如：2024-2025-1）")
    private String semester;

    @ApiModelProperty(value = "学年（如：2024-2025）")
    private String academicYear;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 课程信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Course course;

    /**
     * 教室信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Classroom classroom;

    /**
     * 教师信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Teacher teacher;
}
