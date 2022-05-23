package com.jay.shoppingmall.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
public class PasswordCheckRequest {

    @NotEmpty
    private String password;
}
