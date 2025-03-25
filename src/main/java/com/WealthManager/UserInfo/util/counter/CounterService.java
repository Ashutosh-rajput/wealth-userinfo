package com.WealthManager.UserInfo.util.counter;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterRepository counterRepository;


    public String getNextUserInfoId() {
        SecureRandom random = new SecureRandom();
        int randomFourDigits = 1000 + random.nextInt(9000);
        Counter counter = counterRepository.findByCollectionName("userinfo");
        if (counter == null) {
            counter = new Counter("userinfo");
        }
        counter.increment();
        counterRepository.save(counter);
        return "USER" + String.format("%010d", counter.getValue());
//        return "USER" + randomFourDigits + String.format("%06d", counter.getValue());
    }

    public Long getNextRefreshTokeId() {
        SecureRandom random = new SecureRandom();
        int randomFourDigits = 1000 + random.nextInt(9000);
        Counter counter = counterRepository.findByCollectionName("refreshToken");
        if (counter == null) {
            counter = new Counter("refreshToken");
        }
        counter.increment();
        counterRepository.save(counter);
        return 10000*randomFourDigits + counter.getValue();
    }

//    public String getNextChildId() {
//        Counter counter = counterRepository.findByCollectionName("children");
//        if (counter == null) {
//            counter = new Counter("children");
//        }
//        counter.increment();
//        counterRepository.save(counter);
//        return "CHI" + String.format("%010d", counter.getValue());
//    }


}
