package com.cheese.springjpa.properties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AntiSamplePropertiesRunner implements ApplicationRunner {

    private final Environment env;

    @Override
    public void run(ApplicationArguments args) {
        final String email = env.getProperty("user.email");
        final String name = env.getProperty("user.name");
        final int age = Integer.valueOf(env.getProperty("user.age"));
        final boolean auth = Boolean.valueOf(env.getProperty("user.auth"));
        final int amount = Integer.valueOf(env.getProperty("user.amount"));


        log.info("=========ANTI=========");
        log.info(email);
        log.info(name);
        log.info(String.valueOf(age));
        log.info(String.valueOf(auth));
        log.info(String.valueOf(amount));
        log.info("=========ANTI=========");
    }
}
