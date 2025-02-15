package com.WealthManager.UserInfo.configuration;//package com.trainingmug.practiceplatform.bootstrap;


import com.WealthManager.UserInfo.model.dao.UserInfo;
import com.WealthManager.UserInfo.model.enums.Role;
import com.WealthManager.UserInfo.repo.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultAccountInitializer implements CommandLineRunner {
    /*
    This class is to create few default accounts
     */

    private final UserInfoRepo userInfoRepo;
    private final KafkaService kafkaService;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.mail}")
    private String adminMail;

    @Override
    public void run(String... args)  {
        String email = "admin@trainingmug.com";
        if (userInfoRepo.existsByEmail(email)) return;
        //String password = UUID.randomUUID().toString().substring(0, 20).replace("-", "");
        String password = "Abcd@1234";
        log.info("{} - Admin Password {}", this.getClass().getSimpleName(), password);
        UserInfo adminUser = new UserInfo();
        adminUser.setName("Admin");
        adminUser.setEmail(email);
        adminUser.setPassword(passwordEncoder.encode(password));
        adminUser.setRole(Role.ROLE_SUPER_ADMIN);
        adminUser.setVerified(true);
        adminUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        adminUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userInfoRepo.save(adminUser);

        log.info("{} Admin user created: {}", this.getClass().getSimpleName(), adminUser);
        kafkaService.publishToKafkaAsync(new AdminPasswordGeneratedEvent(adminMail, password, email));


    }


}

