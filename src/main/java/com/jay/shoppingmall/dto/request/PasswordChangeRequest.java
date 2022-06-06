package com.jay.shoppingmall.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeRequest {

    @Pattern(regexp = "^[A-Za-z[0-9]]{10,20}$", message = "비밀번호는 영문과 숫자를 포함하며 10자 이상이어야 합니다")
    @NotBlank
    private String passwordNow;

    @Pattern(regexp = "^[A-Za-z[0-9]]{10,20}$", message = "비밀번호는 영문과 숫자를 포함하며 10자 이상이어야 합니다")
    @NotBlank
    private String passwordAfter;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z[0-9]]{10,20}$", message = "비밀번호는 영문과 숫자를 포함하며 10자 이상이어야 합니다")
    private String repeatPasswordAfter;
}
