package com.WealthManager.UserInfo.configuration;

import com.Ashutosh.RedisCache.CacheService;
import org.attachment.softnerve.service.KafkaService;
import org.attachment.softnerve.service.KafkaServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public KafkaService kafkaService(){
        return new KafkaServiceImpl();
    }
    @Bean
    public CacheService cacheService(){
        return new CacheService();
    }
}
