package com.jay.shoppingmall.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AgreeRequest {

    private Boolean isMandatoryAgree;

    private Boolean isMarketingAgree;
}
