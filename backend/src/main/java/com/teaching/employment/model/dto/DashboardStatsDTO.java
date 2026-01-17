package com.teaching.employment.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Dashboard统计数据DTO
 */
@Data
@Schema(description = "Dashboard统计数据")
public class DashboardStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "教师数量")
    private Integer teacherCount;

    @Schema(description = "学生数量")
    private Integer studentCount;

    @Schema(description = "课程数量")
    private Integer courseCount;

    @Schema(description = "就业率")
    private Double employmentRate;

    @Schema(description = "待审阅日报数量")
    private Integer pendingReports;

    @Schema(description = "正在求职的学员数量")
    private Integer seekingStudents;

    @Schema(description = "待确认Offer数量")
    private Integer pendingOffers;

    @Schema(description = "新学员数量")
    private Integer newStudents;
}
