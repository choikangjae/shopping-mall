package com.jay.shoppingmall.dto.response.user;

import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.domain.user.model.Name;
import com.jay.shoppingmall.domain.user.model.PhoneNumber;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailResponse {

    private String email;

    private Role role;

    private String fullAddress;

    private String fullName;

    private String fullPhoneNumber;

    private Boolean isMandatoryAgree;

    private Boolean isMarketingAgree;
}
