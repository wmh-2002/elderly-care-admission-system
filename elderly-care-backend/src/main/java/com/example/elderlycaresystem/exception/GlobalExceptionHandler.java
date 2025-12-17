package com.example.elderlycaresystem.exception;

import com.example.elderlycaresystem.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理器
 * 统一处理系统中抛出的异常，并返回标准化的错误响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param request HTTP请求对象
     * @param e       业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Object> handleBusinessException(HttpServletRequest request, BusinessException e) {
        log.error("业务异常, 请求URL: {}, 异常信息: {}", request.getRequestURL(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     *
     * @param request HTTP请求对象
     * @param e       参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleValidationException(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error("参数校验异常, 请求URL: {}", request.getRequestURL());
        
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        
        for (FieldError fieldError : fieldErrors) {
            errorMsg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数校验失败: " + errorMsg.toString());
    }

    /**
     * 处理绑定异常
     *
     * @param request HTTP请求对象
     * @param e       绑定异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> handleBindException(HttpServletRequest request, BindException e) {
        log.error("绑定异常, 请求URL: {}", request.getRequestURL());
        
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        
        for (FieldError fieldError : fieldErrors) {
            errorMsg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数绑定失败: " + errorMsg.toString());
    }

    /**
     * 处理所有未捕获的异常
     *
     * @param request HTTP请求对象
     * @param e       异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(HttpServletRequest request, Exception e) {
        log.error("系统异常, 请求URL: {}", request.getRequestURL(), e);
        return ApiResponse.error("系统内部错误，请稍后重试");
    }
}