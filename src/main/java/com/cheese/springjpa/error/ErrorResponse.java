package com.cheese.springjpa.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String message;
    private String code;
    private int status;
    private List<FieldError> errors;

    @Builder
    public ErrorResponse(String message, String code, int status, List<FieldError> errors) {
        this.message = message;
        this.code = code;
        this.status = status;
        this.errors = errors;
    }

    public static class FieldError {
        private String propertyPath;
        private String inValidValue;
        private String message;
    }

}
