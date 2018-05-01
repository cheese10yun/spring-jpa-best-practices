package com.cheese.springjpa.Account.exception;

import lombok.Getter;

@Getter
public class EmailDuplicationException extends RuntimeException {

    private String email;
    private String field;

    public EmailDuplicationException(String email) {
        this.field = "email";
        this.email = email;
    }
}
