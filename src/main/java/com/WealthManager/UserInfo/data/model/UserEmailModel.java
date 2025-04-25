package com.WealthManager.UserInfo.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEmailModel implements Serializable {

    @Serial
    private static final long serialVersionUID=87823472938783L;

    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String otp;
    private String registrationToken;
    private String contactUsUrl="https://WealthArc/support.com";


}
