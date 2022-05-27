package com.jay.shoppingmall.exception;

import com.jay.shoppingmall.exception.exceptions.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorList = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errorList);
        return handleExceptionInternal(ex, errorDetails, headers, errorDetails.getStatus(), request);
    }

//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
//        final ErrorResponse errorResponse = ErrorResponse.builder()
//                .code("VALIDATION_FAILED")
//                .message(e.getMessage())
//                .build();
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }

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
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<?> handleUserException(AlreadyExistsException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ALREADY_EXISTS")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<?> handleUserException(ItemNotFoundException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ITEM_NOT_FOUND")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}