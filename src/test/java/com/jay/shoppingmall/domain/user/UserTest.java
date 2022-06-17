package com.jay.shoppingmall.domain.user;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.domain.user.model.Name;
import com.jay.shoppingmall.domain.user.model.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    User user = User.builder().build();

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        final Address address = EntityBuilder.getAddress();
        final Name name = EntityBuilder.getName();
        final Agree agree = EntityBuilder.getAgree();
        final PhoneNumber phoneNumber = EntityBuilder.getPhoneNumber();

        this.user.userUpdate(address, name, agree, phoneNumber);
    }

    @Test
    void returnUsername_getUsername() {
        final String username = user.getUsername();
        assertThat(username).isEqualTo("qwe");
    }
    @Test
    @DisplayName("@을 포함하지 않음")
    void itShouldNot_ContainAt_getUsername() {
        final String username = user.getUsername();
        assertThat(username).doesNotContain("@");
    }
}