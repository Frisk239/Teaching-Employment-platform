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
 * 课程实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_course")
@ApiModel(description = "课程信息")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "课程ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "课程名称")
    private String courseName;

    @ApiModelProperty(value = "课程代码")
    private String courseCode;

    @ApiModelProperty(value = "课程类型（普通课程/直播课程）")
    private String courseType;

    @ApiModelProperty(value = "开课学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "授课教师ID")
    private Long teacherId;

    @ApiModelProperty(value = "教室ID")
    private Long classroomId;

    @ApiModelProperty(value = "课程描述")
    private String description;

    @ApiModelProperty(value = "学分")
    private BigDecimal credit;

    @ApiModelProperty(value = "总课时")
    private Integer totalHours;

    @ApiModelProperty(value = "开课日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @ApiModelProperty(value = "结课日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @ApiModelProperty(value = "最大学员数")
    private Integer maxStudents;

    @ApiModelProperty(value = "当前学员数")
    private Integer currentStudents;

    @ApiModelProperty(value = "状态：pending-未开始 ongoing-进行中 completed-已完成 cancelled-已取消")
    private String status;

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
     * 学校信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private School school;

    /**
     * 教师信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Teacher teacher;

    /**
     * 教室信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Classroom classroom;

    /**
     * 教师姓名（来自教师和用户表）
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 学校名称（来自学校表）
     */
    @TableField(exist = false)
    private String schoolName;

    /**
     * 教室名称（来自教室表）
     */
    @TableField(exist = false)
    private String classroomName;
}
