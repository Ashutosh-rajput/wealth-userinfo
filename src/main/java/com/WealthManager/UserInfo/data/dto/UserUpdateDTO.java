package com.WealthManager.UserInfo.data.dto;

import com.WealthManager.UserInfo.data.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserUpdateDTO {
    private String userId;
    @NotNull(message = "Name Shouldn't be null")
    private String name;
    @Pattern(regexp = "^\\d{10}$",message = "invalid mobile number entered")
    private String phoneNumber;
    private Gender gender;
    @NotBlank(message = "Date of birth must not be blank")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Invalid date format (DD/MM/YYYY)")
    private String dob;
}
