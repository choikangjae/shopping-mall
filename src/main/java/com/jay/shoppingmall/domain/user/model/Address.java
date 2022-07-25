package com.jay.shoppingmall.domain.user.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Address implements Serializable {

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

    public String getFullAddress() {
        if (extraAddress == null) {
            return String.format("%s %s", this.address, this.detailAddress);
        }
        return String.format("%s %s %s", this.address, this.detailAddress, extraAddress);
    }
}
