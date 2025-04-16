package com.WealthManager.UserInfo.security;

import com.WealthManager.UserInfo.data.dao.UserInfo;
import com.WealthManager.UserInfo.exception.UserNotVerifiedException;
import com.WealthManager.UserInfo.repo.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomUserInfoDetailService implements UserDetailsService {

    private final UserInfoRepo userInfoRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo userInfo = Optional.ofNullable(userInfoRepo.findByEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        log.info("User found with email for login: " + email);
        if (!userInfo.isVerified()) {
            throw new UserNotVerifiedException("Please verify your email.");
        }

        return new AuthUser(userInfo);
    }

}