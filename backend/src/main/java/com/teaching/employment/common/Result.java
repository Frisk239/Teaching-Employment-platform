package com.teaching.employment.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 通用响应结果类
 *
 * @param <T> 数据泛型
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "通用响应结果")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    @ApiModelProperty(value = "响应码")
    private Integer code;

    /**
     * 响应消息
     */
    @ApiModelProperty(value = "响应消息")
    private String message;

    /**
     * 响应数据
     */
    @ApiModelProperty(value = "响应数据")
    private T data;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据泛型
     * @return Result
     */
    public static <T> Result<T> ok() {
        return new Result<>(200, "操作成功");
    }

    /**
     * 成功响应（有数据）
     *
     * @param data 响应数据
     * @param <T>  数据泛型
     * @return Result
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     *
     * @param message 响应消息
     * @param <T>     数据泛型
     * @return Result
     */
    public static <T> Result<T> ok(String message) {
        return new Result<>(200, message);
    }

    /**
     * 成功响应（自定义消息和数据）
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据泛型
     * @return Result
     */
    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败响应
     *
     * @param <T> 数据泛型
     * @return Result
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败");
    }

    /**
     * 失败响应（自定义消息）
     *
     * @param message 错误消息
     * @param <T>     数据泛型
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }

    /**
     * 失败响应（自定义状态码和消息）
     *
     * @param code    响应码
     * @param message 错误消息
     * @param <T>     数据泛型
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 失败响应（自定义状态码、消息和数据）
     *
     * @param code    响应码
     * @param message 错误消息
     * @param data    响应数据
     * @param <T>     数据泛型
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 未授权响应
     *
     * @param <T> 数据泛型
     * @return Result
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权，请先登录");
    }

    /**
     * 禁止访问响应
     *
     * @param <T> 数据泛型
     * @return Result
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "无权限访问");
    }

    /**
     * 资源未找到响应
     *
     * @param <T> 数据泛型
     * @return Result
     */
    public static <T> Result<T> notFound() {
        return new Result<>(404, "资源未找到");
    }

    // Getter and Setter methods

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
