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
    private List<String> message;

    public ErrorDetails(HttpStatus status, List<String> errors) {
        super();
        this.status = status;
        this.message = errors;
    }

    public ErrorDetails(HttpStatus status, String error) {
        super();
        this.status = status;
        message = Arrays.asList(error);
    }
}