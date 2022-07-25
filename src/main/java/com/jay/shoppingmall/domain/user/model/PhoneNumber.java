package com.jay.shoppingmall.domain.user.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PhoneNumber implements Serializable {

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
    public String getFullNumber() {
        if (this.first == null || this.middle == null || this.last == null) {
            return "";
        }
        return String.format("%s-%s-%s", this.first, this.middle, this.last);
    }
}


