package com.jay.shoppingmall.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MeDetailResponse {

    private Long id;

    private String email;

    private String address;

    private String fullName;

    private String phoneNumber;
}
