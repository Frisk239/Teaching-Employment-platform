package com.teaching.employment.constants;

/**
 * 系统通用常量
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public class Constants {

    /**
     * UTF-8字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "success";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "fail";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "登录成功";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "登录失败";

    /**
     * 验证码Redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_code:";

    /**
     * 登录用户Redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_token:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌有效期（分钟） 7天
     */
    public static final Integer TOKEN_EXPIRATION = 10080;

    /**
     * 默认每页记录数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页记录数
     */
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * 文件上传路径
     */
    public static final String UPLOAD_PATH = "upload/";

    /**
     * 头像上传路径
     */
    public static final String AVATAR_PATH = UPLOAD_PATH + "avatar/";

    /**
     * 附件上传路径
     */
    public static final String ATTACHMENT_PATH = UPLOAD_PATH + "attachment/";

    /**
     * Excel模板路径
     */
    public static final String EXCEL_TEMPLATE_PATH = "template/excel/";

    /**
     * 是
     */
    public static final Integer YES = 1;

    /**
     * 否
     */
    public static final Integer NO = 0;

    /**
     * 正常状态
     */
    public static final Integer STATUS_NORMAL = 1;

    /**
     * 停用状态
     */
    public static final Integer STATUS_DISABLE = 0;

    /**
     * 已删除
     */
    public static final Integer DELETED = 1;

    /**
     * 未删除
     */
    public static final Integer NOT_DELETED = 0;

    /**
     * 用户性别-男
     */
    public static final Integer GENDER_MALE = 1;

    /**
     * 用户性别-女
     */
    public static final Integer GENDER_FEMALE = 2;
}
