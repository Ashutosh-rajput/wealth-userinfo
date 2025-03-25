package com.WealthManager.UserInfo.security.Interface;


import com.WealthManager.UserInfo.data.dao.RefreshToken;

import java.util.Optional;

public interface RefreshTokenServiceInterface {
    RefreshToken createRefreshToken(String username);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
}
