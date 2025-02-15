package com.WealthManager.UserInfo.repo;

import com.WealthManager.UserInfo.model.dao.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserInfoRepo extends MongoRepository<UserInfo,Long> {
    boolean existsByEmail(String email);
    UserInfo findByUsername(String username);
    UserInfo findByUsernameOrEmail(String username,String email);
}
