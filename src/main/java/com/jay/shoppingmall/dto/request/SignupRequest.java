package com.jay.shoppingmall.dto.request;

import com.jay.shoppingmall.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@Builder
public class SignupRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z[0-9]]{10,20}$", message = "비밀번호는 영문과 숫자를 포함하며 10자 이상이어야 합니다")
    private String password;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z[0-9]]{10,20}$", message = "비밀번호는 영문과 숫자를 포함하며 10자 이상이어야 합니다")
    private String repeatPassword;

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .build();
    }
}
