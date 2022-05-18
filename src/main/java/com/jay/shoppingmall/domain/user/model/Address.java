package com.jay.shoppingmall.domain.user.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Address {

    @NotEmpty
    private String zipcode;

    @NotEmpty
    private String address1;

    @NotEmpty
    private String address2;

    @Builder
    public Address(final String zipcode, final String address1, final String address2) {
        this.zipcode = zipcode;
        this.address1 = address1;
        this.address2 = address2;
    }
}
