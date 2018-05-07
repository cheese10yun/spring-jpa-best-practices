package com.cheese.springjpa.Account.exception;

import lombok.Getter;

@Getter
public class EmailDuplicationException extends RuntimeException {

    private com.cheese.springjpa.Account.model.Email email;
    private String field;

    public EmailDuplicationException(com.cheese.springjpa.Account.model.Email email) {
        this.field = "email";
        this.email = email;
    }
}
