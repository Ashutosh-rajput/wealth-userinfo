package com.WealthManager.UserInfo.service;



import com.WealthManager.UserInfo.data.dto.ChangePasswordDTO;
import com.WealthManager.UserInfo.data.dto.SuccessResponse;
import com.WealthManager.UserInfo.data.dto.UserRegistrationDTO;
import com.WealthManager.UserInfo.data.dto.UserUpdateDTO;

public interface UserInfoService {
    SuccessResponse registerUser(UserRegistrationDTO  userDTO);

    SuccessResponse verifyUser(UserUpdateDTO userUpdateDTO);

    SuccessResponse getUserById(Long id);

    SuccessResponse getUserByName(String username);

    SuccessResponse getUserByPhoneNumber(String phoneNumber);

    SuccessResponse getAllUsers();

    SuccessResponse deleteUserById(Long id);

    SuccessResponse deleteAllUsers();

    SuccessResponse updateUserById(UserUpdateDTO userUpdateDTO, Long userId);

    SuccessResponse deleteUserByID(Long userId);

    SuccessResponse getUserByEmail(String email);

    boolean isUserExist(String email);

    SuccessResponse changePassword(ChangePasswordDTO changePasswordDTO);

    SuccessResponse forgotPassword(String email);

    SuccessResponse updatePasswordByOtp(UserUpdateDTO userUpdateDTO);



}
