package com.example.teaching.employment.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 需要关注的学员DTO
 */
@Data
@Schema(description = "需要关注的学员信息")
public class StudentAttentionDTO {

    @Schema(description = "学员ID")
    private Long id;

    @Schema(description = "学员姓名")
    private String realName;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "求职状态")
    private String seekingStatus;

    @Schema(description = "班级")
    private String className;
}
