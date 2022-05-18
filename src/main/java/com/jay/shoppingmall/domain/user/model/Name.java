package com.jay.shoppingmall.domain.user.model;

import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Name {

    @NotEmpty
    @Column(name = "family_name", length = 50)
    private String family;

    @NotEmpty
    @Column(name = "personal_name", length = 50)
    private String personal;

    @Builder
    public Name(final String family, final String personal) {
        this.family = family;
        this.personal = personal;
    }
    public String getFullName() {
        return String.format("%s %s", this.family, this.personal);
    }
}
