package com.cheese.springjpa.Account.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {

    private long id;

    public AccountNotFoundException(long id) {
        this.id = id;
    }
}
