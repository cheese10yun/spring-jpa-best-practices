package com.cheese.springjpa.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "user")
@Getter
@Setter
public class SampleProperties {
    private String email;
    private String name;
    private int age;
    private boolean auth;
    private double amount;
}
