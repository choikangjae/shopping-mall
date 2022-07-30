package com.jay.shoppingmall.exception;

import com.jay.shoppingmall.exception.exceptions.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
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
                .distinct()
                .collect(Collectors.toList());
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, errorList);
        return handleExceptionInternal(ex, errorDetails, headers, errorDetails.getStatus(), request);
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<?> handleNotificationException(NotificationException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("NOTIFICATION_NOT_AVAILABLE")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<?> handleReviewException(ReviewException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("REVIEW_NOT_AVAILABLE")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(MoneyTransactionException.class)
    public ResponseEntity<?> handleMoneyTransactionException(MoneyTransactionException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("FINANCE_TRANSACTION_ERROR")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(DeliveryException.class)
    public ResponseEntity<?> handleDeliveryException(DeliveryException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("DELIVERY_NOT_AVAILABLE")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(StockInvalidException.class)
    public ResponseEntity<?> handleStockInvalidException(StockInvalidException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("STOCK_OVERLOADED")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(OptionDuplicatedException.class)
    public ResponseEntity<?> handleOptionDuplicatedException(OptionDuplicatedException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("OPTION_DUPLICATED")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
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
    public ResponseEntity<?> handleQuantityException(QuantityException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("STOCK_NOT_VALID")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<?> handleAlreadyExistsException(AlreadyExistsException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ALREADY_EXISTS")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<?> handleItemNotFoundException(ItemNotFoundException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ITEM_NOT_FOUND")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<?> handlePasswordInvalidException(PasswordInvalidException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("PASSWORD_NOT_VALID")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<?> handleNotValidException(NotValidException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("NOT_VALID")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(QnaException.class)
    public ResponseEntity<?> handleQnaException(QnaException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("QNA_ERROR")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
