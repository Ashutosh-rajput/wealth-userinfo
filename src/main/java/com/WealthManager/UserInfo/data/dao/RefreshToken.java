package com.WealthManager.UserInfo.data.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(collection = "refreshToken")
public class RefreshToken {

    @Id
    @Field("_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Indexed(unique = true)
    private Long id;
    private String token;
    private Instant expiryDate;

    private String userid;
}
