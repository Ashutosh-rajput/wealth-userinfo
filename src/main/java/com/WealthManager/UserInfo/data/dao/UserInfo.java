package com.WealthManager.UserInfo.data.dao;

import com.WealthManager.UserInfo.data.enums.Gender;
import com.WealthManager.UserInfo.data.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(collection = "users")
public class UserInfo {

    @Version
    private Long version;


    @Id
    @Field("_id")
    @Indexed(unique = true)
    private String userId;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    @Size(max = 12, min = 10)
    private String phoneNumber;

    @Field(targetType = FieldType.STRING)
    private Gender gender;

    @NotBlank
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Invalid date format (DD/MM/YYYY)")
    private String dob;

    private Integer age;

    private String registrationToken;

    private boolean isVerified;

    @Field(targetType = FieldType.STRING)
    private Role role;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;


}
