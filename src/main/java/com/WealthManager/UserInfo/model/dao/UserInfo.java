package com.WealthManager.UserInfo.model.dao;

import com.WealthManager.UserInfo.model.enums.Gender;
import com.WealthManager.UserInfo.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(collection = "users")
public class UserInfo{

    @Version
    private Long version;


    @Id
    @Field("_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Indexed(unique = true)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    @Size(max = 12, min = 10)
    private String mobile;

    @Field(targetType = FieldType.STRING)
    private Gender gender;
    private Integer age;
    private String registrationToken;
    private boolean isVerified;
    @Field(targetType = FieldType.STRING)
    private Role role;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;


//    private String roles;

}
