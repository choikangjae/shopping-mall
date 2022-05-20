package com.jay.shoppingmall.exception;

import com.jay.shoppingmall.exception.exceptions.AgreeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AgreeException.class)
    public ResponseEntity<?> handleAgreeException(AgreeException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("MANDATORY_REQUIRED")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
