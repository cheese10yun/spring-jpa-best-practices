package com.cheese.springjpa.error;

import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import com.cheese.springjpa.Account.exception.EmailDuplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
@Slf4j
public class ErrorExceptionController {

    @ExceptionHandler(value = {
            AccountNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleAccountNotFoundException(AccountNotFoundException e) {
        final ErrorCode accountNotFound = ErrorCode.ACCOUNT_NOT_FOUND;
        log.error(accountNotFound.getMessage(), e.getId());
        return buildError(accountNotFound);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        final BindingResult bindingResult = e.getBindingResult();
        final List<FieldError> errors = bindingResult.getFieldErrors();

        return buildFieldErrors(
                ErrorCode.INPUT_VALUE_INVALID,
                errors.parallelStream()
                        .map(error -> ErrorResponse.FieldError.builder()
                                .reason(error.getDefaultMessage())
                                .field(error.getField())
                                .value((String) error.getRejectedValue())
                                .build())
                        .collect(Collectors.toList())
        );
    }

//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    protected ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
//        log.error(e.getMessage());
//        final Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
//
//        return buildFieldErrors(
//                ErrorCode.INPUT_VALUE_INVALID,
//                constraintViolations.parallelStream()
//                        .map(error -> ErrorResponse.FieldError.builder()
//                                .reason(error.getMessage())
//                                .value(error.getInvalidValue().toString())
//                                .field(error.getPropertyPath().toString())
//                                .build())
//                        .collect(Collectors.toList())
//        );
//    }


    @ExceptionHandler(EmailDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleConstraintViolationException(EmailDuplicationException e) {
        final ErrorCode errorCode = ErrorCode.EMAIL_DUPLICATION;
        log.error(errorCode.getMessage(), e.getEmail());
        return buildError(errorCode);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return buildError(ErrorCode.INPUT_VALUE_INVALID);
    }


    // TODO: 2018. 5. 12. 비밀번호 변경 컨트롤러 생성시 주석 해제할것 -yun
//    @ExceptionHandler(PasswordFailedExceededException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    protected ErrorResponse handlePasswordFailedExceededException(PasswordFailedExceededException e) {
//        log.error(e.getMessage());
//        return buildError(e.getErrorCode());
//    }


    private ErrorResponse buildError(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .build();
    }

    private ErrorResponse buildFieldErrors(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();
    }
}
