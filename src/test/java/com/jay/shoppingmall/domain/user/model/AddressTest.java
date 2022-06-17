package com.jay.shoppingmall.domain.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    Address address = Address.builder().build();
    Address addressWithNoExtra = Address.builder().build();

    @BeforeEach
    void setUp() {
        address = Address.builder()
                .address("서울시 종로")
                .detailAddress("집")
                .zipcode("01010")
                .extraAddress("추가")
                .build();
        addressWithNoExtra = Address.builder()
                .address("서울시 종로")
                .detailAddress("집")
                .zipcode("01010")
                .build();
    }

    @Test
    void WhenWithExtra_getFullAddress() {
        final String fullAddress = address.getFullAddress();

        assertThat(fullAddress).contains("서울시 종로");
        assertThat(fullAddress).contains("집");
        assertThat(fullAddress).contains("추가");
        assertThat(fullAddress).doesNotContain("01010");
    }
    @Test
    void WhenWithoutExtra_getFullAddress() {
        final String fullAddress = addressWithNoExtra.getFullAddress();

        assertThat(fullAddress).contains("서울시 종로");
        assertThat(fullAddress).contains("집");
        assertThat(fullAddress).doesNotContain("추가");
        assertThat(fullAddress).doesNotContain("01010");
    }
}