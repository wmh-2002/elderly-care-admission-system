package com.example.elderlycaresystem.controller;

import com.example.elderlycaresystem.dto.ApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础控制器类
 * 提供系统通用的API接口
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 允许跨域请求
public class BaseController {

    /**
     * 健康检查接口
     * 
     * @return 系统状态
     */
    @RequestMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("OK", "系统运行正常");
    }
}