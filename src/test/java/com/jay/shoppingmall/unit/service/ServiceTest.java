package com.jay.shoppingmall.unit.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.SignupRequest;
import com.jay.shoppingmall.exception.exceptions.UserDuplicatedException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;
    @Spy
    PasswordEncoder passwordEncoder;

    SignupRequest signupRequest;

    //    @BeforeEach
//    void setUp() throws Exception {
//
//    }

    @Test
    void 회원가입_실패_이메일_형식미달() throws UserDuplicatedException{
        //mocking
        signupRequest = SignupRequest.builder()
                .email("qwe")
                .password("qwe213123123")
                .build();

        when(userRepository.save(any(User.class))).thenThrow(
                UserDuplicatedException.class
        );
        //when
        authService.signup(signupRequest);
        //then
        assertThrows(UserDuplicatedException.class, () -> {authService.signup(signupRequest);
        });
    }



    @Test
    void 회원가입_성공() {
        //mocking
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        SignupRequest signupRequest = SignupRequest.builder()
                .email("qwe@qwe")
                .password("qwe213123123")
                .build();
        String encryptedPw = passwordEncoder.encode(signupRequest.getPassword());

        given(userRepository.save(any(User.class))).willReturn(User.builder()
                .email("qwe@qwe")
                .password(encryptedPw)
                .build());

        //when
        User signupUser = authService.signup(signupRequest);
        //then
        assertThat(signupUser).isNotNull();
        assertThat(signupUser.getEmail()).isEqualTo(signupRequest.getEmail());
        assertThat(passwordEncoder.matches(signupRequest.getPassword(), signupUser.getPassword())).isTrue();
    }
}
