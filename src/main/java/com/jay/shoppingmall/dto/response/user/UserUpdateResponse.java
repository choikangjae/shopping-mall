package com.jay.shoppingmall.dto.response.user;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateResponse {

    private String firstName;
    private String lastName;
    private String zipcode;
    private String address;
    private String detailAddress;
    private String extraAddress;
    private String phoneNumber;
}
