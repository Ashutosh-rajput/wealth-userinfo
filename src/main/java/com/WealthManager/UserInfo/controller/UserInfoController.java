package com.WealthManager.UserInfo.controller;


import com.WealthManager.UserInfo.constant.ApiConstant;
import com.WealthManager.UserInfo.data.dto.*;
import com.WealthManager.UserInfo.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @PostMapping(ApiConstant.SAVE_USER)
    public ResponseEntity<SuccessResponse> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        return new ResponseEntity<>(userInfoService.registerUser(userRegistrationDTO), HttpStatus.CREATED);
    }

    @GetMapping(ApiConstant.VERIFY_USER)
    public ResponseEntity<SuccessResponse> verifyUser(@RequestParam String email, @RequestParam String token) {
        return new ResponseEntity<>(userInfoService.verifyUser(email, token), HttpStatus.OK);
    }

    @GetMapping(ApiConstant.GET_USER_BY_ID)
    public ResponseEntity<SuccessResponse> getUserById(@PathVariable String userId) {
        return new ResponseEntity<>(userInfoService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping(ApiConstant.GET_USER_BY_EMAIL)
    public ResponseEntity<SuccessResponse> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userInfoService.getUserByEmail(email), HttpStatus.OK);
    }

    @GetMapping(ApiConstant.GET_ALL_USERS)
    public ResponseEntity<SuccessResponse> getAllUsers(@PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(userInfoService.getAllUsers(page, size), HttpStatus.OK);
    }

    @PutMapping(ApiConstant.UPDATE_USER_BY_ID)
    public ResponseEntity<SuccessResponse> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return new ResponseEntity<>(userInfoService.updateUserById(userUpdateDTO, userId), HttpStatus.OK);
    }

    @DeleteMapping(ApiConstant.DELETE_USER_BY_ID)
    public ResponseEntity<SuccessResponse> deleteUser(@PathVariable String userId) {
        return new ResponseEntity<>(userInfoService.deleteUserById(userId), HttpStatus.OK);
    }

    @PutMapping(ApiConstant.CHANGE_PASSWORD)
    public ResponseEntity<SuccessResponse> changePassword(@RequestParam String email, @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        return new ResponseEntity<>(userInfoService.changePassword(email, changePasswordDTO), HttpStatus.OK);
    }

    @PostMapping(ApiConstant.FORGOT_PASSWORD)
    public ResponseEntity<SuccessResponse> forgotPassword(@PathVariable String email) {
        return new ResponseEntity<>(userInfoService.forgotPassword(email), HttpStatus.OK);
    }

    @PostMapping(ApiConstant.RESET_PASSWORD)
    public ResponseEntity<SuccessResponse> resetPassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return new ResponseEntity<>(
                userInfoService.updatePasswordByOtp(updatePasswordDTO),
                HttpStatus.OK
        );
    }
}
