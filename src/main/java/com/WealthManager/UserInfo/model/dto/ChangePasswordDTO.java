package com.WealthManager.UserInfo.model.dto;

import com.softnerve.Patient_MicroService.Validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO  implements Serializable {

    @Serial
    private static final long serialVersionUID = 854588728238L;

    @NotBlank(message = "Current password must not be blank")
    private String currentPassword;
    @NotBlank(message = "New password must not be blank")
    @ValidPassword
    private String newPassword;
    @NotBlank(message = "Confirm password must not be blank")
    @ValidPassword
    private String confirmPassword;
}
