package com.cheese.springjpa.error;

import lombok.Builder;
import lombok.Getter;

@Getter
public enum ErrorCode {

    ACCOUNT_NOT_FOUND("AC_001", "해당 회원을 찾을 수 없습니다.", 404);


    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
