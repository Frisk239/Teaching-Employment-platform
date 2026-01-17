package com.teaching.employment.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 日报信息DTO
 */
@Data
@Schema(description = "日报信息")
public class DailyReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日报ID")
    private Long id;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "报告日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    @Schema(description = "日报内容")
    private String content;

    @Schema(description = "学习时长")
    private Integer studyHours;

    @Schema(description = "状态")
    private String status;
}
