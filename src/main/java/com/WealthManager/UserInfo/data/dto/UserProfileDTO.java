package com.WealthManager.UserInfo.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private String patientUserName;
    private String patientFirstName;
    private String patientLastName;
    private String email;
    private String phoneNumber;
    private String firstLane;
    private String secondLane;
}
