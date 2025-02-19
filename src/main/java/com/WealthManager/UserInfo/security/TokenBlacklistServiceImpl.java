package com.WealthManager.UserInfo.security;

import com.Ashutosh.ReportGenerator.Service.ServiceInterface.TokenBlacklistServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistServiceInterface {
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addToBlacklist(String token) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", 7, TimeUnit.DAYS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}
