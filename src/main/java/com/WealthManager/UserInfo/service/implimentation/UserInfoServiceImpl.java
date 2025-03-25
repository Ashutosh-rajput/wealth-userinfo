package com.WealthManager.UserInfo.service.implimentation;

import com.Ashutosh.RedisCache.CacheService;
import com.WealthManager.UserInfo.constant.ApiConstant;
import com.WealthManager.UserInfo.converter.UserInfoConverter;
import com.WealthManager.UserInfo.data.dao.UserInfo;
import com.WealthManager.UserInfo.data.dto.*;
import com.WealthManager.UserInfo.data.enums.Role;
import com.WealthManager.UserInfo.data.model.UserProfileModel;
import com.WealthManager.UserInfo.exception.*;
import com.WealthManager.UserInfo.repo.UserInfoRepo;
import com.WealthManager.UserInfo.service.UserInfoService;
import com.WealthManager.UserInfo.util.counter.CounterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.attachment.softnerve.service.KafkaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepo userInfoRepo;
    private final KafkaService kafkaService;
    private final CacheService cacheService;
    private final CounterService counterService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public SuccessResponse registerUser(UserRegistrationDTO userDTO) {
        log.info("Validating and creating a new user.");
        long startTime = System.currentTimeMillis();
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userDTO.setEmail(userDTO.getEmail().toLowerCase());
        this.checkEmailExists(userDTO);
        this.checkPhoneNumberExists(userDTO);
        UserInfo userInfo = UserInfoConverter.toEntity(userDTO);
        userInfo.setAge(calculateAge(userInfo.getDob()));
        userInfo.setRegistrationToken(generateRegistrationToken());
        log.info("Registration token:- {}", userInfo.getRegistrationToken());
        userInfo.setVerified(false);
        userInfo.setRole(Role.ROLE_USER);
        userInfo.setUserId(counterService.getNextUserInfoId());
        userInfoRepo.save(userInfo);
        log.info("Data storing in Mongo took {} ms", (System.currentTimeMillis() - startTime));
        log.info("Publishing Kafka event to send verification email for user with ID: {}", userInfo.getUserId());
        kafkaService.publishToKafkaAsync("SendVerificationEmail", userInfo.getUserId(), userInfo.toString());

        // Return the ApiResponse using the constant path
        return new SuccessResponse(
                HttpStatus.CREATED.value(),
                "Please verify email to register successfully.",
                ApiConstant.SAVE_USER,
                null
        );
    }

    @Override
    public SuccessResponse verifyUser(String email, String registrationToken) {
        log.info("Verifying user with email: {}", email);
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(email, "verifyUser");
        if (userInfo == null) {
            log.error("User with email: {} not found", email);
            throw new UserNotFoundException("User not found wth email: " + email);
        }
        if (!userInfo.getRegistrationToken().equals(registrationToken)) {
            log.error("User with email: {} not matching registration token", email);
            throw new BadRequestException("Registration token does not match");
        }
        userInfo.setVerified(true);
        userInfoRepo.save(userInfo);
        cacheService.addEntry("USER_INFO", userInfo.getEmail(), userInfo);
        cacheService.addEntry("USER_INFO", userInfo.getUserId(), userInfo);
        kafkaService.publishToKafkaAsync("SendRegistrationSuccessful", userInfo.getUserId(), userInfo.toString());
        log.info("User with email: {} successfully verified", email);
        return new SuccessResponse(
                HttpStatus.ACCEPTED.value(),
                "User registered successfully with email: " + userInfo.getEmail(),
                ApiConstant.VERIFY_USER,
                new SuccessResponse.ResponseData<>(Collections.emptyList(), null)
        );
    }

    @Override
    public SuccessResponse getUserById(String userId) {
        log.info("Getting user by id: {}", userId);
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(userId, "getUserById");
        UserProfileModel userProfileModel = UserInfoConverter.toModel(userInfo);
        return new SuccessResponse(
                HttpStatus.FOUND.value(),
                "User found with Id.",
                ApiConstant.GET_USER_BY_ID,
                new SuccessResponse.ResponseData<>(Collections.singletonList(userProfileModel), null)
        );
    }

    @Override
    public SuccessResponse getAllUsers(int page, int size) {
        log.info("Getting all users");
        try {
            long startTime = System.currentTimeMillis();
            List<Object> cachedObjects = cacheService.getAllEntries(page, size, "USER_INFO");
            log.info("Cache hit for getAllUsers");
            List<UserInfo> users = cachedObjects.stream()
                    .filter(obj -> obj instanceof UserInfo)
                    .map(obj -> (UserInfo) obj)
                    .toList();

            List<UserProfileModel> userProfiles = users.stream()
                    .map(UserInfoConverter::toModel)
                    .toList();

            long endTime = System.currentTimeMillis();
            log.info("Data fetch took {} ms from cache.", (endTime - startTime));

            return new SuccessResponse(
                    HttpStatus.OK.value(),
                    "Users fetched successfully",
                    ApiConstant.GET_ALL_USERS,
                    new SuccessResponse.ResponseData<>(userProfiles, null)
            );
        } catch (CacheEntryNotFoundException e) {
            log.info("Cache miss for getAllUsers - Fetching from database");
            long startTimeDb = System.currentTimeMillis();
            List<UserInfo> users = userInfoRepo.findAll();

            if (users.isEmpty()) {
                log.info("No users found");
                return new SuccessResponse(
                        HttpStatus.OK.value(),
                        "No users found",
                        ApiConstant.GET_ALL_USERS,
                        new SuccessResponse.ResponseData<>(Collections.emptyList(), null)
                );
            }

            List<UserProfileModel> userProfiles = users.stream()
                    .map(UserInfoConverter::toModel)
                    .toList();

            // Cache the results for future calls
            cacheService.addAllEntries(new ArrayList<>(users), page, size, "USER_INFO");

            long endTimeDb = System.currentTimeMillis();
            log.info("Data fetch took {} ms from DB", (endTimeDb - startTimeDb));

            return new SuccessResponse(
                    HttpStatus.OK.value(),
                    "Users fetched successfully",
                    ApiConstant.GET_ALL_USERS,
                    new SuccessResponse.ResponseData<>(userProfiles, null)
            );
        }
    }

    @Override
    public SuccessResponse deleteUserById(String userId) {
        log.info("Deleting user with id: {}", userId);
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(userId, "deleteUserById");
        if (userInfo == null) {
            log.error("User with id: {} not found", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        userInfoRepo.delete(userInfo);

        // Remove from cache
        cacheService.deleteEntry("USER_INFO", userInfo.getUserId());
        cacheService.deleteEntry("USER_INFO", userInfo.getEmail());

        // Publish Kafka event for user deletion
        kafkaService.publishToKafkaAsync("UserDeleted", userInfo.getUserId(), userInfo.toString());

        return new SuccessResponse(
                HttpStatus.OK.value(),
                "User deleted successfully",
                ApiConstant.DELETE_USER_BY_ID,
                null
        );
    }

    @Override
    public SuccessResponse deleteAllUsers() {
        log.info("Deleting all users");
        userInfoRepo.deleteAll();


        cacheService.deleteAllKeys();

        return new SuccessResponse(
                HttpStatus.OK.value(),
                "All users deleted successfully",
                ApiConstant.DELETE_ALL_USERS,
                null
        );
    }

    @Override
    public SuccessResponse updateUserById(UserUpdateDTO userUpdateDTO, String userId) {
        log.info("Updating user with id: {}", userId);
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(userId, "updateUserById");
        if (userInfo == null) {
            log.error("User with id: {} not found", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        // Update user information
        if (userUpdateDTO.getName() != null) {
            userInfo.setName(userUpdateDTO.getName());
        }
        if (userUpdateDTO.getPhoneNumber() != null) {
            // Check if phone number already exists for another user
            if (!userInfo.getPhoneNumber().equals(userUpdateDTO.getPhoneNumber()) &&
                    userInfoRepo.existsByPhoneNumber(userUpdateDTO.getPhoneNumber())) {
                throw new PhoneNumberAlreadyExists("Phone number already exists. Please use another phone number.");
            }
            userInfo.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }
        if (userUpdateDTO.getDob() != null) {
            userInfo.setDob(userUpdateDTO.getDob());
            userInfo.setAge(calculateAge(userInfo.getDob()));
        }
        if (userUpdateDTO.getGender() != null) {
            userInfo.setGender(userUpdateDTO.getGender());
        }

        userInfo.setUpdatedAt(new Date());
        userInfoRepo.save(userInfo);

        // Update cache
        cacheService.addEntry("USER_INFO", userInfo.getUserId(), userInfo);
        cacheService.addEntry("USER_INFO", userInfo.getEmail(), userInfo);


        UserProfileModel userProfileModel = UserInfoConverter.toModel(userInfo);
        return new SuccessResponse(
                HttpStatus.OK.value(),
                "User updated successfully",
                ApiConstant.UPDATE_USER_BY_ID,
                new SuccessResponse.ResponseData<>(Collections.singletonList(userProfileModel), null)
        );
    }


    @Override
    public SuccessResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(email.toLowerCase(), "getUserByEmail");
        if (!userInfo.isVerified()) {
            throw new UserNotVerifiedException("Please verify your email.");
        }
        UserProfileModel userProfileModel = UserInfoConverter.toModel(userInfo);
        return new SuccessResponse(
                HttpStatus.FOUND.value(),
                "User found with email",
                ApiConstant.GET_USER_BY_EMAIL,
                new SuccessResponse.ResponseData<>(Collections.singletonList(userProfileModel), null)
        );
    }

    @Override
    public boolean isUserExist(String email) {
        log.info("Checking if user exists with email: {}", email);
        try {
            String key = cacheService.generateKey("USER_INFO", email.toLowerCase());
            Object cachedUser = cacheService.getEntry(key);
            log.info("Cache hit for isUserExist - Key: {}", key);
            return cachedUser != null;
        } catch (CacheEntryNotFoundException e) {
            log.info("Cache miss for isUserExist - Checking database");
            return userInfoRepo.existsByEmail(email.toLowerCase());
        }
    }

    @Override
    public SuccessResponse changePassword(String email, ChangePasswordDTO changePasswordDTO) {
        log.info("Changing password for user with email: {}", email);
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(email.toLowerCase(), "changePassword");

        // Verify old password
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), userInfo.getPassword())) {
            log.error("Invalid old password for user with email: {}", email);
            throw new BadRequestException("Invalid old password");
        }
        // Verify new password
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            log.error("New password doesn't match with confirm password user with email: {}", email);
            throw new BadRequestException("Invalid request.");
        }

        // Update password
        userInfo.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userInfoRepo.save(userInfo);

        // Update cache
        cacheService.addEntry("USER_INFO", userInfo.getUserId(), userInfo);
        cacheService.addEntry("USER_INFO", userInfo.getEmail(), userInfo);

        // Publish Kafka event for password change
        kafkaService.publishToKafkaAsync("PasswordChanged", userInfo.getUserId(), "Password changed successfully");

        return new SuccessResponse(
                HttpStatus.OK.value(),
                "Password changed successfully",
                ApiConstant.CHANGE_PASSWORD,
                null
        );
    }

    @Override
    public SuccessResponse forgotPassword(String email) {
        log.info("Processing forgot password request for email: {}", email);
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(email, "forgotPassword");
        if (!userInfo.isVerified()) {
            throw new UserNotVerifiedException("Please verify your email.");
        }
        String otp = generateOTP();
        // Publish Kafka event for password change otp
        kafkaService.publishToKafkaAsync("SendPasswordChangedOtpEmail", userInfo.getUserId(), userInfo.toString());
        cacheService.addOtp("USER_OTP", email, otp, 300);

        return new SuccessResponse(
                HttpStatus.OK.value(),
                "OTP sent successfully",
                ApiConstant.FORGOT_PASSWORD.replace("{email}", email),
                new SuccessResponse.ResponseData<>(Collections.emptyList(), null)
        );

    }

    @Override
    public SuccessResponse updatePasswordByOtp(UpdatePasswordDTO updatePasswordDTO) {
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(updatePasswordDTO.getEmail(), "updatePasswordByOtp");
        cacheService.verifyOtp("USER_OTP", updatePasswordDTO.getEmail(), updatePasswordDTO.getOTP());
        userInfo.setPassword(passwordEncoder.encode(updatePasswordDTO.getPassword()));
        userInfoRepo.save(userInfo);
        cacheService.addEntry("USER_INFO", userInfo.getUserId(), userInfo);
        cacheService.addEntry("USER_INFO", userInfo.getEmail(), userInfo);
        return SuccessResponse.builder()
                .statusCode(200)
                .message("Password updated successfully")
                .path(ApiConstant.RESET_PASSWORD)
                .responseData(null)
                .build();
    }

    @Override
    public SuccessResponse getUserByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public SuccessResponse getUserByName(String name) {

        return null;
    }
//------------------------------Helper-Methods----------------------------------------------------

    private UserInfo getUserInfoFromCacheOrDBByIdOrEmail(String userIdOrEmail, String methodName) {
        String key = cacheService.generateKey("USER_INFO", userIdOrEmail);
        try {
            UserInfo userInfo = (UserInfo) cacheService.getEntry(key);
            log.info("Cache hit for {} - Key: {}", methodName, key);
            return userInfo;
        } catch (CacheEntryNotFoundException e) {
            log.info("Cache miss for {} - Fetching from database", methodName);
            String userIdPattern = "^USER\\d{10}$";
            if (userIdOrEmail.matches(userIdPattern)) {
                UserInfo userInfo=userInfoRepo.findById(userIdOrEmail)
                        .orElseThrow(() -> new UserNotFoundException("User with email " + userIdOrEmail + " not found"));
                cacheService.addEntry("USER_INFO", userInfo.getUserId(), userInfo);
                return userInfo;

            }
            UserInfo userInfo = userInfoRepo.findByEmail(userIdOrEmail);
            if (userInfo == null) {
                throw new UserNotFoundException("User with email " + userIdOrEmail + " not found");

            }
            cacheService.addEntry("USER_INFO", userInfo.getUserId(), userInfo);
            return userInfo;


        }
    }

    // Method to check if the phone number already exists
    private void checkPhoneNumberExists(UserRegistrationDTO userRegistrationDTO) {
        if (userInfoRepo.existsByPhoneNumber(userRegistrationDTO.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExists("Phone number already exists. Please use another phone number.");
        }
    }

    // Method to check if the email already exists
    private void checkEmailExists(UserRegistrationDTO userRegistrationDTO) {
        if (userInfoRepo.existsByEmail(userRegistrationDTO.getEmail())) {
            kafkaService.publishToKafkaAsync("SendAlreadyRegisteredEmail", userRegistrationDTO.getUserId(), userRegistrationDTO.toString());
            throw new EmailAlreadyExists("Email already exists. Please use another email.");
        }
    }

    private int calculateAge(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate birthDate = LocalDate.parse(dob, formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    private String generateRegistrationToken() {
        return UUID.randomUUID().toString();
    }

    // Generates a random 4-digit OTP.
    private String generateOTP() {
        Random rand = new Random();
        return String.format("%04d", rand.nextInt(10000));
    }


}