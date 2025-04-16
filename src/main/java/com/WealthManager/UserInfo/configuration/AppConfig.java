package com.WealthManager.UserInfo.configuration;

import org.attachment.softnerve.service.KafkaService;
import org.attachment.softnerve.service.KafkaServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public KafkaService kafkaService() {
        return new KafkaServiceImpl();
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
