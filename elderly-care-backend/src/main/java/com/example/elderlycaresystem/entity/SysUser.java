package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 对应数据库表 sys_user
 */
@Data
@TableName("sys_user")
public class SysUser {

    /**
     * 用户主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码（BCrypt 密文）
     */
    private String pwd;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 外键→sys_role
     */
    private Long roleId;

    /**
     * 状态：1 正常  0 禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}