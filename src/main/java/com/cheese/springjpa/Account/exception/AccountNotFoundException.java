package com.cheese.springjpa.Account.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(long id) {
        super(id + " is not exited");
    }
}
