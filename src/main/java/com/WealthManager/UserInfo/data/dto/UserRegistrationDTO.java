package com.WealthManager.UserInfo.data.dto;

import com.WealthManager.UserInfo.Validation.ValidPassword;
import com.WealthManager.UserInfo.data.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegistrationDTO {
    private String userId;
    @NotNull(message = "Username Shouldn't be null")
    private String name;
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    @ValidPassword
    private String password;
    @Email
    private String email;
    @Pattern(regexp = "^\\d{10}$",message = "invalid mobile number entered")
    private String phoneNumber;
    private Gender gender;
    @NotBlank(message = "Date of birth must not be blank")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Invalid date format (DD/MM/YYYY)")
    private String dob;

}
