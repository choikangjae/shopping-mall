package com.jay.shoppingmall.dto.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
public class UserUpdateRequest {

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String address;

    @NotBlank
    private String detailAddress;

    private String extraAddress;

    @NotBlank
    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$", message = "전화번호 형식에 맞지않습니다")
    private String phoneNumber;

    private Boolean isMarketingAgree;
}
