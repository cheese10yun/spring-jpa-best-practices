package com.cheese.springjpa.properties;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "user")
@Validated
@Getter
@Setter
public class SampleProperties {
    @Email
    private String email;

    @NotEmpty
    private String nickname;

    private int age;

    private boolean auth;

    private double amount;
}
