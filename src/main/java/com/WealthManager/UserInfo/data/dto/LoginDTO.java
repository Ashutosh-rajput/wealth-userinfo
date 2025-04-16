package com.WealthManager.UserInfo.data.dto;

import com.WealthManager.UserInfo.Validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginDTO {
    @Email
    private String email;
    @ValidPassword
    private String password;
}
