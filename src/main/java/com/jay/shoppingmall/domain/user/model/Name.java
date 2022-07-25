package com.jay.shoppingmall.domain.user.model;

import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Optional;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Name implements Serializable {

    @NotEmpty
    @Column(name = "last_name", length = 50)
    private String last;

    @NotEmpty
    @Column(name = "first_name", length = 50)
    private String first;

    @Builder
    public Name(final String first, final String last) {
        this.last = last;
        this.first = first;
    }
    public String getFullName() {
        return String.format("%s%s", this.last, this.first);
    }
}
