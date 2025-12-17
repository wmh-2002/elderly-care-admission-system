package com.example.elderlycaresystem.servicelmpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.elderlycaresystem.entity.SysUser;
import com.example.elderlycaresystem.exception.BusinessException;
import com.example.elderlycaresystem.repository.SysUserRepository;
import com.example.elderlycaresystem.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户服务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    public SysUser getUserById(Long id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return sysUserRepository.selectById(id);
    }

    @Override
    public SysUser getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException("用户名不能为空");
        }
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return sysUserRepository.selectOne(wrapper);
    }

    @Override
    public List<SysUser> getAllUsers() {
        return sysUserRepository.selectList(null);
    }

    @Override
    public Long saveUser(SysUser sysUser) {
        if (sysUser == null) {
            throw new BusinessException("用户信息不能为空");
        }
        
        // 检查用户名是否已存在
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", sysUser.getUsername());
        if (sysUser.getId() != null) {
            // 更新操作，排除当前用户
            wrapper.ne("id", sysUser.getId());
        }
        int count = sysUserRepository.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        if (sysUser.getId() == null) {
            // 新增用户
            sysUser.setCreateTime(LocalDateTime.now());
            sysUserRepository.insert(sysUser);
            return sysUser.getId();
        } else {
            // 更新用户
            sysUserRepository.updateById(sysUser);
            return sysUser.getId();
        }
    }

    @Override
    public boolean updateUser(SysUser sysUser) {
        if (sysUser == null || sysUser.getId() == null) {
            throw new BusinessException("用户信息不能为空");
        }
        
        // 检查用户名是否已存在
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", sysUser.getUsername()).ne("id", sysUser.getId());
        int count = sysUserRepository.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        int result = sysUserRepository.updateById(sysUser);
        return result > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        int result = sysUserRepository.deleteById(id);
        return result > 0;
    }

    @Override
    public SysUser login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名和密码不能为空");
        }
        
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        SysUser user = sysUserRepository.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException("用户名不存在");
        }
        
        // 这里需要实现密码校验逻辑，实际项目中应该使用BCrypt等加密方式
        // 这里简化处理，直接比较密码
        if (!password.equals(user.getPwd())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }
        
        return user;
    }
}