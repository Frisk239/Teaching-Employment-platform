package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户活动记录实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Data
@TableName("t_user_activity")
@ApiModel(description = "用户活动记录")
public class UserActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "活动类型：login-登录 logout-登出 profile_update-资料修改 password_change-密码修改 course_enroll-课程注册 homework_submit-作业提交 report_submit-日报提交 job_apply-职位申请 test_complete-笔试完成 interview_attend-面试通知 offer_received-收到offer")
    private String activityType;

    @ApiModelProperty(value = "活动标题")
    private String title;

    @ApiModelProperty(value = "活动描述")
    private String description;

    @ApiModelProperty(value = "关联对象ID")
    private Long relatedId;

    @ApiModelProperty(value = "关联对象类型")
    private String relatedType;

    @ApiModelProperty(value = "IP地址")
    private String ipAddress;

    @ApiModelProperty(value = "用户代理")
    private String userAgent;

    @ApiModelProperty(value = "活动时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 用户信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private User user;

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String username;

    /**
     * 真实姓名
     */
    @TableField(exist = false)
    private String realName;
}
