package com.example.elderlycaresystem.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一API响应结果封装类
 *
 * @param <T> 响应数据类型
 */
@Data
public class ApiResponse<T> implements Serializable {

    /**
     * 响应码：成功
     */
    public static final int SUCCESS_CODE = 200;
    
    /**
     * 响应码：失败
     */
    public static final int ERROR_CODE = 500;
    
    /**
     * 响应码：未授权
     */
    public static final int UNAUTHORIZED_CODE = 401;
    
    /**
     * 响应码：禁止访问
     */
    public static final int FORBIDDEN_CODE = 403;
    
    /**
     * 响应码：资源未找到
     */
    public static final int NOT_FOUND_CODE = 404;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 私有构造函数，防止外部直接实例化
     */
    private ApiResponse() {}

    /**
     * 成功响应，带数据
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    /**
     * 成功响应，带数据和消息
     *
     * @param data    响应数据
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 成功响应，仅消息
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> successMsg(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setMessage(message);
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ERROR_CODE);
        response.setMessage(message);
        return response;
    }

    /**
     * 自定义错误码和消息
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 未授权响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(UNAUTHORIZED_CODE);
        response.setMessage(message != null ? message : "未授权访问");
        return response;
    }

    /**
     * 资源未找到响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> notFound(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(NOT_FOUND_CODE);
        response.setMessage(message != null ? message : "资源未找到");
        return response;
    }

    /**
     * 检查响应是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }
}