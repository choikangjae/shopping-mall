package com.jay.shoppingmall.domain.user.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agree {

    @Column
    private Boolean isMandatoryAgree;

    @Column
    private Boolean isMarketingAgree;


}
