package com.WealthManager.UserInfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.WealthManager.UserInfo", "com.Ashutosh.RedisCache"})
public class UserInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserInfoApplication.class, args);
	}

}
