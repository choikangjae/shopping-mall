package com.jay.shoppingmall.dto.request.password;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {

    private String password;
}
