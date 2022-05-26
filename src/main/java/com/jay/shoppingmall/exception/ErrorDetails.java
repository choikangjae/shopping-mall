package com.jay.shoppingmall.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorDetails {
    private HttpStatus status;
//    private String message;
    private List<String> errors;

    public ErrorDetails(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
//        this.message = message;
        this.errors = errors;
    }

    public ErrorDetails(HttpStatus status, String message, String error) {
        super();
        this.status = status;
//        this.message = message;
        errors = Arrays.asList(error);
    }
}