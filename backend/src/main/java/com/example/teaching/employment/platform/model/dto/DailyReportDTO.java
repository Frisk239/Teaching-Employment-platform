package com.example.teaching.employment.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 日报DTO
 */
@Data
@Schema(description = "日报信息")
public class DailyReportDTO {

    @Schema(description = "日报ID")
    private Long id;

    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "学员姓名")
    private String studentName;

    @Schema(description = "报告日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    @Schema(description = "日报内容")
    private String content;

    @Schema(description = "学习时长（小时）")
    private Integer studyHours;

    @Schema(description = "状态")
    private String status;
}
