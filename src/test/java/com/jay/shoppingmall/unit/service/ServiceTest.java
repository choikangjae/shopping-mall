package com.jay.shoppingmall.unit.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.UserValidationRequest;
import com.jay.shoppingmall.exception.exceptions.UserDuplicatedException;
import com.jay.shoppingmall.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;
    @Spy
    PasswordEncoder passwordEncoder;

    UserValidationRequest userValidationRequest;

    //    @BeforeEach
//    void setUp() throws Exception {
//
//    }

    @Test
    void 회원가입_실패_이메일_형식미달() throws UserDuplicatedException{
        //mocking
        userValidationRequest = UserValidationRequest.builder()
                .email("qwe")
                .password("qwe213123123")
                .build();

        when(userRepository.save(any(User.class))).thenThrow(
                UserDuplicatedException.class
        );
        //when
        authService.userRegistration(userValidationRequest);
        //then
        assertThrows(UserDuplicatedException.class, () -> {authService.userRegistration(userValidationRequest);
        });
    }



    @Test
    void 회원가입_성공() {
        //mocking
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserValidationRequest userValidationRequest = UserValidationRequest.builder()
                .email("qwe@qwe")
                .password("qwe213123123")
                .build();
        String encryptedPw = passwordEncoder.encode(userValidationRequest.getPassword());

        given(userRepository.save(any(User.class))).willReturn(User.builder()
                .email("qwe@qwe")
                .password(encryptedPw)
                .build());

        //when
        User signupUser = authService.userRegistration(userValidationRequest);
        //then
        assertThat(signupUser).isNotNull();
        assertThat(signupUser.getEmail()).isEqualTo(userValidationRequest.getEmail());
        assertThat(passwordEncoder.matches(userValidationRequest.getPassword(), signupUser.getPassword())).isTrue();
    }
}
