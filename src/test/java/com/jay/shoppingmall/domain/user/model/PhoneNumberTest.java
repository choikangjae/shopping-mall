package com.jay.shoppingmall.domain.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberTest {

    PhoneNumber phoneNumber = PhoneNumber.builder().build();

    @BeforeEach
    void setUp() {
        phoneNumber = PhoneNumber.builder()
                .first("010")
                .middle("1234")
                .last("5678")
                .build();
    }

    @Test
    void getFullNumber() {
        final String fullNumber = phoneNumber.getFullNumber();

        assertThat(fullNumber).isEqualTo("010-1234-5678");
        assertThat(fullNumber).isNotEqualTo("01012345678");
        assertThat(fullNumber).contains("010");
        assertThat(fullNumber).contains("1234");
        assertThat(fullNumber).contains("5678");
    }
}