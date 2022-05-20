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
    private String address;

    @NotEmpty
    private String detailAddress;

    private String extraAddress;

    @Builder
    public Address(final String zipcode, final String address, final String detailAddress, final String extraAddress) {
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.extraAddress = extraAddress;
    }
}
