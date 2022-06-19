package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.token.password.PasswordResetToken;
import com.jay.shoppingmall.domain.token.password.PasswordResetTokenRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.UserValidationRequest;
import com.jay.shoppingmall.dto.request.password.PasswordChangeRequest;
import com.jay.shoppingmall.dto.request.password.PasswordResetRequest;
import com.jay.shoppingmall.exception.exceptions.TokenExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;
    @Mock
    UserRepository userRepository;
    @Mock
    SellerRepository sellerRepository;
    @Mock
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MailService mailService;

    UserValidationRequest userValidationRequest;
    PasswordResetRequest passwordResetRequest;

    @BeforeEach
    void setUp() {
        passwordResetRequest = new PasswordResetRequest("token", "qwe@qwe");

        userValidationRequest = UserValidationRequest.builder()
                .email("qwe@qwe")
                .password("qweqweqwe1")
                .repeatPassword("qweqweqwe1")
                .build();
    }

    @Test
    void userRegistration() {
        when(passwordEncoder.encode(any())).thenReturn(any());

        authService.userRegistration(userValidationRequest);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void sellerSignup() {
        when(passwordEncoder.encode(any())).thenReturn(any());

        authService.sellerSignup(userValidationRequest);

        verify(userRepository).save(any(User.class));
        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void passwordTokenSender() {
        User user = User.builder()
                .email("qwe@qwe")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        authService.passwordTokenSender(passwordResetRequest);

        verify(mailService).sendMail(any(), any());
        verify(passwordResetTokenRepository).save(any());
    }

    @Test
    void tokenIsValid_NotThrowException_passwordTokenValidator() {
        final String email = "qwe@qwe";
        final String token = "token";

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .email(email)
                .isExpired(false)
                .build();
        when(passwordResetTokenRepository.findByEmailAndToken(email, token)).thenReturn(Optional.of(passwordResetToken));

        authService.passwordTokenValidator(passwordResetRequest);
    }
    @Test
    void tokenIsExpired_TriedAfter10Minutes_ThrowTokenExpiredException_passwordTokenValidator() {
        final String email = "qwe@qwe";
        final String token = "token";

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .email(email)
                .isExpired(false)
                .build();
        when(passwordResetTokenRepository.findByEmailAndToken(email, token)).thenReturn(Optional.of(passwordResetToken));
        LocalDateTime defaultTime = LocalDateTime.now().plusMinutes(10);
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(defaultTime);
            assertThrows(TokenExpiredException.class, () -> authService.passwordTokenValidator(passwordResetRequest));
        }
    }

    @Test
    void passwordUpdateAfterReset() {
        User user = EntityBuilder.getUser();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("encryptedPassword");

        authService.passwordUpdateAfterReset(userValidationRequest);

        assertThat(user.getPassword()).isEqualTo("encryptedPassword");
    }

    @Test
    void passwordChange() {
        User user = EntityBuilder.getUser();

        PasswordChangeRequest passwordChangeRequest = PasswordChangeRequest.builder()
                .passwordNow("qweqwe123123")
                .passwordAfter("qweqweqwe2")
                .repeatPasswordAfter("qweqweqwe2")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(passwordChangeRequest.getPasswordNow(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(passwordChangeRequest.getPasswordAfter(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encryptedPassword");

        authService.passwordChange(passwordChangeRequest, user);

        assertThat(user.getPassword()).isNotEqualTo("qweqwe123123");
        assertThat(user.getPassword()).isEqualTo("encryptedPassword");
    }
}