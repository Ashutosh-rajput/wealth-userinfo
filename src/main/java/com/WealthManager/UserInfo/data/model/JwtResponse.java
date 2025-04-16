package com.WealthManager.UserInfo.data.model;

import com.WealthManager.UserInfo.data.enums.Gender;
import com.WealthManager.UserInfo.data.enums.Role;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;



    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserResponse {
        private String userId;
        private String name;
        private String email;
        private Role role;
        private Gender gender;
    }
}
