package com.teaching.employment.dto;

import com.teaching.employment.entity.Timetable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 教学计划VO（包含课程时间表信息）
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Data
@ApiModel(description = "教学计划VO")
public class TeachingPlanVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教学计划ID")
    private Long id;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @ApiModelProperty(value = "课次序号")
    private Integer lessonNumber;

    @ApiModelProperty(value = "周次")
    private Integer weekNumber;

    @ApiModelProperty(value = "本次课标题")
    private String title;

    @ApiModelProperty(value = "教学内容描述")
    private String content;

    @ApiModelProperty(value = "课程名称")
    private String courseName;

    @ApiModelProperty(value = "上课时间信息")
    private Timetable timetable;
}
