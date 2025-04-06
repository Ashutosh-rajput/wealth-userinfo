package com.WealthManager.UserInfo.service;



import com.WealthManager.UserInfo.data.dto.*;
import com.WealthManager.UserInfo.data.model.JwtResponse;
import com.WealthManager.UserInfo.data.model.SuccessResponse;

import java.time.Instant;

public interface UserInfoService {
    SuccessResponse registerUser(UserRegistrationDTO  userDTO);

    SuccessResponse verifyUser(String email,String registrationToken);

    JwtResponse login(LoginDTO loginDTO);

    JwtResponse getAccessTokenByRefreshToken(RefreshTokenRequest refreshTokenRequest);

    SuccessResponse getUserById(String userId);

    SuccessResponse getUserByName(String username);

    SuccessResponse getUserByPhoneNumber(String phoneNumber);

    SuccessResponse getAllUsers(int page, int size);

    SuccessResponse deleteUserById(String UserId);

    SuccessResponse deleteAllUsers();

    SuccessResponse updateUserById(UserUpdateDTO userUpdateDTO, String userId);


    SuccessResponse getUserByEmail(String email);

    boolean isUserExist(String email);

    SuccessResponse changePassword(String email,ChangePasswordDTO changePasswordDTO);

    SuccessResponse forgotPassword(String email);

    SuccessResponse updatePasswordByOtp(UpdatePasswordDTO updatePasswordDTO);



}
