package com.elderlycare.management.security;

import com.elderlycare.management.entity.SysUser;
import com.elderlycare.management.repository.SysUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserRepository userRepository;

    public UserDetailsServiceImpl(SysUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            SysUser user = userRepository.findByUsernameWithRole(username)
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

            return UserPrincipal.create(user);
        } catch (Exception e) {
            System.err.println("Error loading user " + username + ": " + e.getMessage());
            throw e;
        }
    }
}

