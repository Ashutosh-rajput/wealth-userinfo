package com.WealthManager.UserInfo.data.model;

import com.WealthManager.UserInfo.data.enums.Role;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private Role role;
}
