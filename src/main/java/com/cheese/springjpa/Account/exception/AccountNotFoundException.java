package com.cheese.springjpa.Account.exception;

import com.cheese.springjpa.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(long id) {
        super(ErrorCode.ACCOUNT_NOT_FOUND.getMessage() + id);
    }
}
