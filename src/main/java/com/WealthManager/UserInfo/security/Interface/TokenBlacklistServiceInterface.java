package com.WealthManager.UserInfo.security.Interface;

public interface TokenBlacklistServiceInterface {
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
}
