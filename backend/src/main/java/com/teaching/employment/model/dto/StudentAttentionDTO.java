package com.teaching.employment.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 需要关注的学员信息DTO
 */
@Data
@Schema(description = "需要关注的学员信息")
public class StudentAttentionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "学生ID")
    private Long id;

    @Schema(description = "学生姓名")
    private String realName;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "求职状态")
    private String seekingStatus;

    @Schema(description = "班级名称")
    private String className;
}
