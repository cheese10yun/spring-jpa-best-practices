package com.cheese.springjpa.Account.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    @org.hibernate.validator.constraints.Email
    @Column(name = "email", nullable = false, unique = true)
    private String address;

    @Builder
    public Email(String address) {
        this.address = address;
    }
}
