package com.jay.shoppingmall.service.common;

import com.jay.shoppingmall.exception.exceptions.TokenExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionUpdaterTest {

    @InjectMocks
    SessionUpdater sessionUpdater;
    @Spy
    AuthenticationManager authenticationManager;

    @Test
    void sessionUpdateToken() {
        String email = "qwe@qwe";
        String rawPassword = "qweqweqwe1";

        sessionUpdater.sessionUpdateToken(email, rawPassword);

        verify(authenticationManager).authenticate(any());
    }
}