package com.teaching.employment.enums;

/**
 * 课程类型枚举
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public enum CourseTypeEnum {

    /**
     * 普通课程
     */
    NORMAL(1, "普通课程", "集中实训等线下课程"),

    /**
     * 直播课程
     */
    LIVE(2, "直播课程", "在线直播课程");

    private final Integer code;
    private final String value;
    private final String description;

    CourseTypeEnum(Integer code, String value, String description) {
        this.code = code;
        this.value = value;
        this.description = description;
    }

    public Integer getCode() {
        return code;
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
    public static CourseTypeEnum getByValue(String value) {
        for (CourseTypeEnum courseTypeEnum : values()) {
            if (courseTypeEnum.getValue().equals(value)) {
                return courseTypeEnum;
            }
        }
        return null;
    }
}
