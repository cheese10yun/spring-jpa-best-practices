package com.cheese.springjpa.account.exception;

import lombok.Getter;

@Getter
public class EmailDuplicationException extends RuntimeException {

    private com.cheese.springjpa.account.model.Email email;
    private String field;

    public EmailDuplicationException(com.cheese.springjpa.account.model.Email email) {
        this.field = "email";
        this.email = email;
    }
}
