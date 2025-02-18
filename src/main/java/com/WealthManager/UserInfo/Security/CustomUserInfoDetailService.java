package com.WealthManager.UserInfo.Security;

import com.WealthManager.UserInfo.exception.ResourceNotFoundException;
import com.WealthManager.UserInfo.model.dao.UserInfo;
import com.WealthManager.UserInfo.repo.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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