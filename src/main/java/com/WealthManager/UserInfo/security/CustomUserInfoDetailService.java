package com.WealthManager.UserInfo.security;

import com.WealthManager.UserInfo.data.dao.UserInfo;
import com.WealthManager.UserInfo.repo.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class CustomUserInfoDetailService implements UserDetailsService {

    private final UserInfoRepo userInfoRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepo.findByEmail(email);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        GrantedAuthority authority = new SimpleGrantedAuthority(userInfo.getRole().toString());
        return new org.springframework.security.core.userdetails.User(
                userInfo.getEmail(),
                userInfo.getPassword(),
                Collections.singletonList(authority)
        );
    }
}