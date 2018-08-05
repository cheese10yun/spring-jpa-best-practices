package com.cheese.springjpa.properties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class SamplePropertiesRunner implements ApplicationRunner {

    private final SampleProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        final String email = properties.getEmail();
        final String name = properties.getNickname();
        final int age = properties.getAge();
        final boolean auth = properties.isAuth();
        final double amount = properties.getAmount();

        log.info("==================");
        log.info(email);
        log.info(name);
        log.info(String.valueOf(age));
        log.info(String.valueOf(auth));
        log.info(String.valueOf(amount));
        log.info("==================");
    }
}
