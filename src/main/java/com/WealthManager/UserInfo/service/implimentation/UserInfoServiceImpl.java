package com.WealthManager.UserInfo.service.implimentation;

import com.WealthManager.UserInfo.constant.ApiConstant;
import com.WealthManager.UserInfo.converter.UserInfoConverter;
import com.WealthManager.UserInfo.data.enums.Role;
import com.WealthManager.UserInfo.exception.EmailAlreadyExists;
import com.WealthManager.UserInfo.exception.PhoneNumberAlreadyExists;
import com.WealthManager.UserInfo.data.dao.UserInfo;
import com.WealthManager.UserInfo.data.dto.ChangePasswordDTO;
import com.WealthManager.UserInfo.data.dto.SuccessResponse;
import com.WealthManager.UserInfo.data.dto.UserRegistrationDTO;
import com.WealthManager.UserInfo.data.dto.UserUpdateDTO;
import com.WealthManager.UserInfo.repo.UserInfoRepo;
import com.WealthManager.UserInfo.service.UserInfoService;
import com.WealthManager.UserInfo.util.counter.CounterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.attachment.softnerve.service.KafkaService;
import org.attachment.softnerve_cache.redis.CacheEntryNotFoundException;
import org.attachment.softnerve_cache.redis.CacheService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

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
    public SuccessResponse verifyUser(UserUpdateDTO userUpdateDTO) {
        return null;
    }

    @Override
    public SuccessResponse getUserById(Long id) {
        return null;
    }

    @Override
    public SuccessResponse getUserByName(String username) {
        return null;
    }

    @Override
    public SuccessResponse getUserByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public SuccessResponse getAllUsers() {
        return null;
    }

    @Override
    public SuccessResponse deleteUserById(Long id) {
        return null;
    }

    @Override
    public SuccessResponse deleteAllUsers() {
        return null;
    }

    @Override
    public SuccessResponse updateUserById(UserUpdateDTO userUpdateDTO, Long userId) {
        return null;
    }

    @Override
    public SuccessResponse deleteUserByID(Long userId) {
        return null;
    }

    @Override
    public SuccessResponse getUserByEmail(String email) {
        return null;
    }

    @Override
    public boolean isUserExist(String email) {
        return false;
    }

    @Override
    public SuccessResponse changePassword(ChangePasswordDTO changePasswordDTO) {
        return null;
    }

    @Override
    public SuccessResponse forgotPassword(String email) {
        return null;
    }

    @Override
    public SuccessResponse updatePasswordByOtp(UserUpdateDTO userUpdateDTO) {
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
                userInfoRepo.findById(userIdOrEmail).map(userInfo -> {
                            cacheService.addEntry("USER_INFO", userInfo.getUserId(), userInfo);
                            return userInfo;
                        })
                        .orElse(null);
            } else {
                UserInfo userInfo = userInfoRepo.findByEmail(userIdOrEmail);
                if (userInfo != null) {
                    cacheService.addEntry("USER_INFO", userInfo.getUserId(), userInfo);
                }
                return userInfo;
            }
            return null;
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


}