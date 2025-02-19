package com.WealthManager.UserInfo.data.dto;


import com.WealthManager.UserInfo.Validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDTO {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "OTP is required")
    private String OTP;

    @NotBlank(message = "Password is required")
//    @Size(min = 8, message = "Password must be at least 8 characters long")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).*$",
//            message = "Password must contain at least one digit, one letter, and one special character")
    @ValidPassword
    private String password;
}
