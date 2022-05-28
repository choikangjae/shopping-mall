package com.jay.shoppingmall.domain.token.password;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetToken {

    private static final Long MAX_EXPIRATION_MINUTE = 10L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private String token;

    @Setter
    private Boolean isExpired;

    private LocalDateTime expirationTime;

    @Builder
    public PasswordResetToken(final String email, final String token, final Boolean isExpired) {
        this.email = email;
        this.token = token;
        this.isExpired = isExpired;
        this.expirationTime = LocalDateTime.now().plusMinutes(MAX_EXPIRATION_MINUTE);
    }
}
