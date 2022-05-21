package com.jay.shoppingmall.exception;

import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.QuantityException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserException(UserNotFoundException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("USER_NOT_FOUND")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(QuantityException.class)
    public ResponseEntity<?> handleUserException(QuantityException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("STOCK_NOT_VALID")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<?> argumentNotValidException(Exception e) {
//        final ErrorResponse errorResponse = ErrorResponse.builder()
//                .code("NOT_VALID_ARGUMENT")
//                .message(e.getMessage())
//                .build();
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//
//    }

}
