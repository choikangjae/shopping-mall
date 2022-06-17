package com.jay.shoppingmall.domain.user;

import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.domain.user.model.Name;
import com.jay.shoppingmall.domain.user.model.PhoneNumber;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
        assertThat(userRepository).isNotNull();
    }

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("qwe@qwe")
                .role(Role.ROLE_USER)
                .password(("qweqweqwe1"))
                .build();
        Address address = Address.builder()
                .address("서울시 종로")
                .detailAddress("집")
                .zipcode("01010")
                .extraAddress("")
                .build();
        PhoneNumber phoneNumber = PhoneNumber.builder()
                .first("010")
                .middle("1234")
                .last("5678")
                .build();
        Name name = Name.builder()
                .last("홍")
                .first("길동")
                .build();
        Agree agree = Agree.builder()
                .isMandatoryAgree(true)
                .isMarketingAgree(true)
                .build();
        user.userUpdate(address, name, agree, phoneNumber);

        userRepository.save(user);
    }

    @Test
    void userShouldBeSaved() {
        final List<User> all = userRepository.findAll();

        assertThat(all).isNotEmpty();
    }

    @Test
    void userShouldBeReturned_FindByEmail() {
        final String EMAIL = "qwe@qwe";
        final User user = userRepository.findByEmail(EMAIL)
                .orElseThrow(() -> new UserNotFoundException(" "));

        assertThat(user.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void returnOptionalEmpty_WrongEmail_FindByEmail() {
        final String EMAIL = "111@111";
        final Optional<User> user = userRepository.findByEmail(EMAIL);

        assertThat(user).isEqualTo(Optional.empty());
    }

    @Test
    void returnUser_findByEmailContaining() {
        final String EMAIL = "qwe";
        final List<User> users = userRepository.findByEmailContaining(EMAIL);

        assertThat(users).isNotEmpty();
        assertThat(users.get(0).getEmail()).contains("qwe");
    }

    @Test
    void returnEmpty_NoMatchedEmail_findByEmailContaining() {
        final String EMAIL = "111";
        final List<User> users = userRepository.findByEmailContaining(EMAIL);

        assertThat(users).isEmpty();
    }

    @Test
    void returnUser_findByPhoneNumber() {
        final Optional<User> user = userRepository.findByPhoneNumber("010", "1234", "5678");

        assertThat(user).isNotEmpty();
    }

    @Test
    void returnEmpty_NoMatchedPhoneNumber_findByPhoneNumber() {
        final Optional<User> user = userRepository.findByPhoneNumber("010", "1111", "5678");

        assertThat(user).isEmpty();
    }
}