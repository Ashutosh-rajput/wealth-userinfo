package com.WealthManager.UserInfo;

import com.WealthManager.UserInfo.configuration.RsaKeyConfigProperties;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SecurityScheme(
        name = "BearerAuth",
        scheme = "Bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
@SpringBootApplication(scanBasePackages = {"com.WealthManager.UserInfo", "com.Ashutosh.RedisCache"})
@EnableConfigurationProperties(RsaKeyConfigProperties.class)
public class UserInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserInfoApplication.class, args);
    }



}

