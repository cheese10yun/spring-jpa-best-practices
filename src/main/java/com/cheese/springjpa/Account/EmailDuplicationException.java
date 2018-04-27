package com.cheese.springjpa.Account;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailDuplicationException extends RuntimeException {

    public EmailDuplicationException(String email) {
        super(email);
        log.error(email + "is Duplication");
    }
}
