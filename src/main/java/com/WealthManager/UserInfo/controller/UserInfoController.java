package com.WealthManager.UserInfo.controller;


import com.WealthManager.UserInfo.constant.ApiConstant;
import com.WealthManager.UserInfo.data.dto.*;
import com.WealthManager.UserInfo.data.model.JwtResponse;
import com.WealthManager.UserInfo.data.model.SuccessResponse;
import com.WealthManager.UserInfo.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "APIs for user operations including registration, login, and user management")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Operation(summary = "Register new user", description = "Registers a new user with email verification")
    @PostMapping(ApiConstant.SAVE_USER)
    public ResponseEntity<SuccessResponse> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        return ResponseEntity.ok(userInfoService.registerUser(userRegistrationDTO));
    }

    @Operation(summary = "Verify user", description = "Verifies a user using email and token")
    @GetMapping(ApiConstant.VERIFY_USER)
    public ResponseEntity<SuccessResponse> verifyUser(@RequestParam String email, @RequestParam String token) {
        return new ResponseEntity<>(userInfoService.verifyUser(email, token), HttpStatus.OK);
    }

    @Operation(summary = "Login", description = "Logs in the user and returns JWT access and refresh tokens")
    @PostMapping(ApiConstant.LOGIN)
    public ResponseEntity<JwtResponse> Login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(userInfoService.login(loginDTO));
    }

    @Operation(summary = "Google Callback", description = "Logs in the user and returns JWT access and refresh tokens by google authorization token")
    @PostMapping(ApiConstant.GOOGLE_CALLBACK)
    public ResponseEntity<JwtResponse> GoogleCallBack(@RequestParam String token) {
        return ResponseEntity.ok(userInfoService.handleGoogleCallback(token));
    }

    @Operation(summary = "Refresh JWT token", description = "Generates a new access token using the refresh token")
    @PostMapping(ApiConstant.REFRESH_TOKEN)
    public ResponseEntity<JwtResponse> RefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return new ResponseEntity<>(userInfoService.getAccessTokenByRefreshToken(refreshTokenRequest), HttpStatus.OK);
    }

    @Operation(summary = "Reset password", description = "Resets the password using OTP verification")
    @PostMapping(ApiConstant.RESET_PASSWORD)
    public ResponseEntity<SuccessResponse> resetPassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return new ResponseEntity<>(userInfoService.updatePasswordByOtp(updatePasswordDTO), HttpStatus.OK);
    }

    @Operation(summary = "Forgot password", description = "Sends OTP to user's email for password reset")
    @PostMapping(ApiConstant.FORGOT_PASSWORD)
    public ResponseEntity<SuccessResponse> forgotPassword(@PathVariable String email) {
        return new ResponseEntity<>(userInfoService.forgotPassword(email), HttpStatus.OK);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves user details by user ID")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping(ApiConstant.GET_USER_BY_ID)
    public ResponseEntity<SuccessResponse> getUserById(@PathVariable String userId) {
        return new ResponseEntity<>(userInfoService.getUserById(userId), HttpStatus.OK);
    }

    @Operation(summary = "Get user", description = "Retrieves user details from security context.")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping(ApiConstant.GET_USER)
    public ResponseEntity<SuccessResponse> getUser() {
        return new ResponseEntity<>(userInfoService.getUser(), HttpStatus.OK);
    }

    @Operation(summary = "Get user by Email", description = "Retrieves user details by email")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping(ApiConstant.GET_USER_BY_EMAIL)
    public ResponseEntity<SuccessResponse> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userInfoService.getUserByEmail(email), HttpStatus.OK);
    }

    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping(ApiConstant.GET_ALL_USERS)
    public ResponseEntity<SuccessResponse> getAllUsers(@PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(userInfoService.getAllUsers(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Update user by ID", description = "Updates user details by user ID")
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping(ApiConstant.UPDATE_USER_BY_ID)
    public ResponseEntity<SuccessResponse> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return new ResponseEntity<>(userInfoService.updateUserById(userUpdateDTO, userId), HttpStatus.OK);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user by ID")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping(ApiConstant.DELETE_USER_BY_ID)
    public ResponseEntity<SuccessResponse> deleteUser(@PathVariable String userId) {
        return new ResponseEntity<>(userInfoService.deleteUserById(userId), HttpStatus.OK);
    }

    @Operation(summary = "Change user password", description = "Changes the user's password")
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping(ApiConstant.CHANGE_PASSWORD)
    public ResponseEntity<SuccessResponse> changePassword(@RequestParam String email, @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        return new ResponseEntity<>(userInfoService.changePassword(email, changePasswordDTO), HttpStatus.OK);
    }





}
