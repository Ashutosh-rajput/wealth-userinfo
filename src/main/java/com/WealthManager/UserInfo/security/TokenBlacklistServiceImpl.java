package com.WealthManager.UserInfo.security;

import com.Ashutosh.RedisCache.CacheService;
import com.WealthManager.UserInfo.security.Interface.TokenBlacklistServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistServiceInterface {
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final CacheService cacheService;


    @Override
    public void addToBlacklist(String token) {
        cacheService.addToSet(BLACKLIST_PREFIX + token, "true", 70000);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return cacheService.exitsInSet(BLACKLIST_PREFIX + token);
    }
}
