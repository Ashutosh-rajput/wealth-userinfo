package com.WealthManager.UserInfo.data.model;

import com.WealthManager.UserInfo.data.enums.Gender;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileModel {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private Integer age;
}
