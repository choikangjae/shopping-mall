package com.jay.shoppingmall.domain.user.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PhoneNumber {

    @NotEmpty
    @Column(name = "first_phone_number", length = 10)
    private String first;

    @NotEmpty
    @Column(name = "middle_phone_number", length = 10)
    private String middle;

    @NotEmpty
    @Column(name = "last_phone_number", length = 10)
    private String last;

    @Builder
    public PhoneNumber(final String first, final String middle, final String last) {
        this.first = first;
        this.middle = middle;
        this.last = last;
    }
}


