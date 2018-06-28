package com.cheese.springjpa.account.exception;

import com.cheese.springjpa.error.ErrorCode;
import lombok.Getter;

@Getter
public class PasswordFailedExceededException extends RuntimeException {

    private ErrorCode errorCode;

    public PasswordFailedExceededException() {
        this.errorCode = ErrorCode.PASSWORD_FAILED_EXCEEDED;
    }
}
