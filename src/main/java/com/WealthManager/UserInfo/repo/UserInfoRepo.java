package com.WealthManager.UserInfo.repo;

import com.WealthManager.UserInfo.data.dao.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserInfoRepo extends MongoRepository<UserInfo,String> {
    boolean existsByEmail(String email);
    UserInfo findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
