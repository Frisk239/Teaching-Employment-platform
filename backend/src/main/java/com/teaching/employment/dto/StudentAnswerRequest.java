package com.teaching.employment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 学生答案请求DTO
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Data
@ApiModel(description = "学生答案请求")
public class StudentAnswerRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目ID", required = true)
    private Long questionId;

    @ApiModelProperty(value = "题目类型", required = true)
    private String questionType;

    @ApiModelProperty(value = "学生答案", required = true)
    private String studentAnswer;
}
