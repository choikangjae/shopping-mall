package com.jay.shoppingmall.unit.repository;

import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

//    @BeforeEach
//    void setup() throws Exception {
//        userRepository.save(user());
//    }

    private User user() {
        return User.builder()
                .email("email@e")
                .password("password123213")
                .role(Role.ROLE_USER)
                .build();
    }

//    @Test
//    void 사용자가_추가된다() {
//        User user = user();
//
//        User savedUser = userRepository.save(user);
//
//        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
//        assertThat(savedUser.getPassword()).isEqualTo(user.getPassword());
//    }

    @Test
    void 비밀번호_조건미달_길이_10글자_미만() {

    }

    @Test
    void 비밀번호_조건미달_영어없음() {
    }

    @Test
    void 비밀번호_조건미달_숫자없음() {

    }

    @Test
    void 이메일로_검색_성공_true() {
        User user = user();
        User savedUser = userRepository.save(user);

        User userFoundByEmail = userRepository.findByEmail(savedUser.getEmail()).orElseThrow(()->
                new UserNotFoundException(""));

        assertThat(savedUser.getEmail()).isEqualTo(userFoundByEmail.getEmail());
        assertThat(userFoundByEmail).isNotNull();

    }
}
