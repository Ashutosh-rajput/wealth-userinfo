package com.WealthManager.UserInfo.converter;

import com.WealthManager.UserInfo.data.dao.UserInfo;
import com.WealthManager.UserInfo.data.dto.UserRegistrationDTO;
import com.WealthManager.UserInfo.data.dto.UserUpdateDTO;
import com.WealthManager.UserInfo.data.model.UserProfileModel;

public class UserInfoConverter {
    public static UserInfo toEntity(UserRegistrationDTO userRegistrationDTO) {
        return UserInfo.builder()
                .name(userRegistrationDTO.getName())
                .password(userRegistrationDTO.getPassword())
                .email(userRegistrationDTO.getEmail())
                .phoneNumber(userRegistrationDTO.getPhoneNumber())
                .gender(userRegistrationDTO.getGender())
                .dob(userRegistrationDTO.getDob())
                .build();
    }

    public static UserInfo toEntity(UserUpdateDTO userUpdateDTO) {
        return UserInfo.builder()
                .userId(userUpdateDTO.getUserId())
                .name(userUpdateDTO.getName())
                .phoneNumber(userUpdateDTO.getPhoneNumber())
                .gender(userUpdateDTO.getGender())
                .dob(userUpdateDTO.getDob())
                .build();
    }

    public static UserProfileModel toModel(UserInfo userInfo) {
        return UserProfileModel.builder()
                .userId(userInfo.getUserId())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .gender(userInfo.getGender())
                .age(userInfo.getAge())
                .build();
    }


}
