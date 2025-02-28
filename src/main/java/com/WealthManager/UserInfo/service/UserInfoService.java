package com.WealthManager.UserInfo.service;



import com.WealthManager.UserInfo.data.dto.ChangePasswordDTO;
import com.WealthManager.UserInfo.data.dto.SuccessResponse;
import com.WealthManager.UserInfo.data.dto.UserRegistrationDTO;
import com.WealthManager.UserInfo.data.dto.UserUpdateDTO;

public interface UserInfoService {
    SuccessResponse registerUser(UserRegistrationDTO  userDTO);

    SuccessResponse verifyUser(String email,String registrationToken);

    SuccessResponse getUserById(String userId);

    SuccessResponse getUserByName(String username);

    SuccessResponse getUserByPhoneNumber(String phoneNumber);

    SuccessResponse getAllUsers();

    SuccessResponse deleteUserById(String UserId);

    SuccessResponse deleteAllUsers();

    SuccessResponse updateUserById(UserUpdateDTO userUpdateDTO, String userId);


    SuccessResponse getUserByEmail(String email);

    boolean isUserExist(String email);

    SuccessResponse changePassword(ChangePasswordDTO changePasswordDTO);

    SuccessResponse forgotPassword(String email);

    SuccessResponse updatePasswordByOtp(UserUpdateDTO userUpdateDTO);



}
