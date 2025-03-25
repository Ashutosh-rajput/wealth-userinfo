package com.WealthManager.UserInfo.security.Interface;

import com.WealthManager.UserInfo.data.dao.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends MongoRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserid(String userId);
}
