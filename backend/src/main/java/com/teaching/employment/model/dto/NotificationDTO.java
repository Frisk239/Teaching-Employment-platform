package com.teaching.employment.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息通知DTO
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-18
 */
@Data
@ApiModel(description = "消息通知信息")
public class NotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "通知ID")
    private Long id;

    @ApiModelProperty(value = "接收用户ID")
    private Long userId;

    @ApiModelProperty(value = "接收用户名")
    private String username;

    @ApiModelProperty(value = "接收用户真实姓名")
    private String realName;

    @ApiModelProperty(value = "通知类型：system-系统 notice-公告 homework-作业 report-日报 job-职位 application-申请 test-笔试 interview-面试 offer-offer")
    private String type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "关联对象ID（如作业ID、职位ID等）")
    private Long relatedId;

    @ApiModelProperty(value = "是否已读：0-未读 1-已读")
    private Integer isRead;

    @ApiModelProperty(value = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime readTime;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
