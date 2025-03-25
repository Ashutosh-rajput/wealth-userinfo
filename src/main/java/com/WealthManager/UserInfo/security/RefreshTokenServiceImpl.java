package com.WealthManager.UserInfo.security;

import com.WealthManager.UserInfo.data.dao.RefreshToken;
import com.WealthManager.UserInfo.data.dao.UserInfo;
import com.WealthManager.UserInfo.exception.RefreshTokenExpiredException;
import com.WealthManager.UserInfo.repo.UserInfoRepo;
import com.WealthManager.UserInfo.security.Interface.RefreshTokenRepo;
import com.WealthManager.UserInfo.security.Interface.RefreshTokenServiceInterface;
import com.WealthManager.UserInfo.util.counter.CounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenServiceInterface {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserInfoRepo userInfoRepo;
    private final CounterService counterService;

    @Override
    public RefreshToken createRefreshToken(String email) {
        UserInfo userInfo = userInfoRepo.findByEmail(email);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepo.findByUserid(userInfo.getUserId());

        RefreshToken refreshToken;
        if (existingTokenOpt.isPresent()) {
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(60000));
        } else {
            refreshToken = RefreshToken.builder()
                    .id(counterService.getNextRefreshTokeId())
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(60000))
                    .userid(userInfo.getUserId())
                    .build();
        }

        return refreshTokenRepo.save(refreshToken);
    }


    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if(refreshToken.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepo.delete(refreshToken);
            throw new RefreshTokenExpiredException(refreshToken.getToken()+" Refresh Token was expired.");

        }
        return refreshToken;
    }


}
