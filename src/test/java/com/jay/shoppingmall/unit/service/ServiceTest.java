package com.jay.shoppingmall.unit.service;

import com.jay.shoppingmall.config.TestConfig;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.UserValidationRequest;
import com.jay.shoppingmall.exception.exceptions.UserDuplicatedException;
import com.jay.shoppingmall.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.PrepareTestInstance;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @InjectMocks
//    @Autowired
    AuthService authService;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    UserValidationRequest userValidationRequest;

    String password = "";
    String encryptedPassword = "";

    @BeforeEach
    void setUp() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        password = "qwe213123123";
        encryptedPassword = passwordEncoder.encode(password);
    }

    @Test
    void 회원가입_성공() {
        //mocking
        UserValidationRequest userValidationRequest = UserValidationRequest.builder()
                .email("qwe@qwe")
                .password("qwe213123123")
                .build();
        String encryptedPw = passwordEncoder.encode(userValidationRequest.getPassword());

        given(userRepository.save(any(User.class))).willReturn(User.builder()
                .email("qwe@qwe")
                .password(encryptedPw)
                .build());

        authService.userRegistration(userValidationRequest);
        //when
//        User signupUser = authService.userRegistration(userValidationRequest);
//        //then
//        assertThat(signupUser).isNotNull();
//        assertThat(signupUser.getEmail()).isEqualTo(userValidationRequest.getEmail());
//        assertThat(passwordEncoder.matches(userValidationRequest.getPassword(), signupUser.getPassword())).isTrue();
    }
}
