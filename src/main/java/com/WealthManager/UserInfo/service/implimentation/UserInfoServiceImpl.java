package com.WealthManager.UserInfo.service.implimentation;

import com.Ashutosh.RedisCache.CacheEntryNotFoundException;
import com.Ashutosh.RedisCache.CacheService;
import com.WealthManager.UserInfo.constant.ApiConstant;
import com.WealthManager.UserInfo.converter.UserInfoConverter;
import com.WealthManager.UserInfo.data.dao.RefreshToken;
import com.WealthManager.UserInfo.data.dao.UserInfo;
import com.WealthManager.UserInfo.data.dto.*;
import com.WealthManager.UserInfo.data.enums.Role;
import com.WealthManager.UserInfo.data.model.JwtResponse;
import com.WealthManager.UserInfo.data.model.SuccessResponse;
import com.WealthManager.UserInfo.data.model.UserEmailModel;
import com.WealthManager.UserInfo.data.model.UserProfileModel;
import com.WealthManager.UserInfo.exception.*;
import com.WealthManager.UserInfo.repo.UserInfoRepo;
import com.WealthManager.UserInfo.security.Interface.RefreshTokenRepo;
import com.WealthManager.UserInfo.security.JwtService;
import com.WealthManager.UserInfo.service.UserInfoService;
import com.WealthManager.UserInfo.util.counter.CounterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.attachment.softnerve.service.KafkaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

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
        kafkaService.publishToKafkaAsync("SendVerificationEmail", userInfo.getUserId(), UserInfoConverter.toEmailModel(userInfo));

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
        kafkaService.publishToKafkaAsync("SendRegistrationSuccessful", userInfo.getUserId(), UserInfoConverter.toEmailModel(userInfo));
        log.info("User with email: {} successfully verified", email);
        return new SuccessResponse(
                HttpStatus.ACCEPTED.value(),
                "User registered successfully with email: " + userInfo.getEmail(),
                ApiConstant.VERIFY_USER,
                new SuccessResponse.ResponseData<>(Collections.emptyList(), null)
        );
    }

    @Override
    public JwtResponse login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        if (authentication.isAuthenticated()) {
            // Get UserDetails from authentication
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Extract role from granted authorities
            String roleName = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElseThrow(() -> new IllegalStateException("No authority found for user"));

            Role role = Role.valueOf(roleName); // Convert back to enum

            RefreshToken refreshToken = this.createRefreshToken(loginDTO.getEmail());
            log.info("Login successfully with email: {} Role: {}", loginDTO.getEmail(), role);
            return JwtResponse.builder()
                    .accessToken(jwtService.generateToken(loginDTO.getEmail(), role))
                    .refreshToken(refreshToken.getToken())
                    .email(loginDTO.getEmail())
                    .role(role) // Include role in response if needed
                    .build();
        } else {
            throw new UsernameNotFoundException("invalid User Request");
        }
    }


    @Override
    public JwtResponse getAccessTokenByRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        return this.findByToken(refreshTokenRequest.getRefreshToken())
                .map(this::verifyExpiration)
                .map(refreshToken -> {
                    String email = refreshToken.getEmail();

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    String roleName = userDetails.getAuthorities().stream()
                            .findFirst()
                            .map(GrantedAuthority::getAuthority)
                            .orElseThrow(() -> new IllegalStateException("No authority found for user"));

                    String accessToken = jwtService.generateToken(email, Role.valueOf(roleName));
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.getRefreshToken())
                            .email(refreshToken.getEmail())
                            .build();
                }).orElseThrow(() -> new RefreshTokenNotFoundException(
                        "Refresh token is not in database"));
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
        kafkaService.publishToKafkaAsync("UserDeletedEmail", userInfo.getUserId(), userInfo.toString());

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
        kafkaService.publishToKafkaAsync("PasswordChanged", userInfo.getUserId(), UserInfoConverter.toEmailModel(userInfo));

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
        UserEmailModel userEmailModel = UserInfoConverter.toEmailModel(userInfo);
        userEmailModel.setOtp(otp);
        kafkaService.publishToKafkaAsync("SendPasswordChangedOtpEmail", userInfo.getUserId(), userEmailModel);
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
        kafkaService.publishToKafkaAsync("PasswordChangedEmail", userInfo.getUserId(), UserInfoConverter.toEmailModel(userInfo));

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
                UserInfo userInfo = userInfoRepo.findById(userIdOrEmail)
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
//            kafkaService.publishToKafkaAsync("SendAlreadyRegisteredEmail", userRegistrationDTO.getUserId(), userRegistrationDTO.toString());
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
        log.info("here 30");
        return UUID.randomUUID().toString();
    }

    // Generates a random 4-digit OTP.
    private String generateOTP() {
        Random rand = new Random();
        return String.format("%04d", rand.nextInt(10000));
    }


    //-------------------------Security----------------------------------------------------------------
    private RefreshToken createRefreshToken(String email) {
        UserInfo userInfo = getUserInfoFromCacheOrDBByIdOrEmail(email, "createRefreshToken");
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepo.findByUserid(userInfo.getUserId());
        RefreshToken refreshToken;
        if (existingTokenOpt.isPresent()) {
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(60000));
        } else {
            refreshToken = RefreshToken.builder()
                    .id(counterService.getNextRefreshTokeId())
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(60000))
                    .email(userInfo.getEmail())
                    .build();
        }
        return refreshTokenRepo.save(refreshToken);
    }

    private Optional<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepo.findByToken(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(refreshToken);
            throw new RefreshTokenExpiredException(refreshToken.getToken() + " Refresh Token was expired.");

        }
        return refreshToken;
    }


}