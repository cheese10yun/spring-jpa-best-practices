package com.cheese.springjpa.error;

import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ControllerAdvice
@Slf4j
public class ErrorController {

    @ExceptionHandler(value = {
            AccountNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ErrorResponse handleNotFoundException(RuntimeException ex) {
        log.error(ErrorCode.ACCOUNT_NOT_FOUND.getMessage());
        return buildError(ErrorCode.ACCOUNT_NOT_FOUND);
    }


    private ErrorResponse buildError(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .build();
    }

    private ErrorResponse buildFieldError(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();
    }
}
