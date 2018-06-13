package com.cheese.springjpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(
        basePackageClasses = {Jsr310JpaConverters.class},  // basePackageClasses에 지정
        basePackages = {"com"})
public class SpringJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaApplication.class, args);
    }


}
