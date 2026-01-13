package com.teaching.employment.enums;

/**
 * 求职申请状态枚举
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public enum ApplicationStatusEnum {

    /**
     * 待审核
     */
    PENDING("pending", "待审核"),

    /**
     * 简历筛选
     */
    RESUME_REVIEW("resume_review", "简历筛选"),

    /**
     * 笔试
     */
    WRITTEN_TEST("written_test", "笔试"),

    /**
     * 面试
     */
    INTERVIEW("interview", "面试"),

    /**
     * 录用
     */
    OFFER("offer", "录用"),

    /**
     * 拒绝
     */
    REJECTED("rejected", "拒绝");

    private final String value;
    private final String description;

    ApplicationStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据value获取枚举
     */
    public static ApplicationStatusEnum getByValue(String value) {
        for (ApplicationStatusEnum statusEnum : values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
