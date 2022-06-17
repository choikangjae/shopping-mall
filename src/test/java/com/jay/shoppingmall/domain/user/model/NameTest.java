package com.jay.shoppingmall.domain.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class NameTest {

    Name name = Name.builder().build();

    @BeforeEach
    void setUp() {
        name = Name.builder()
                .last("홍")
                .first("길동")
                .build();
    }
    @Test
    void getFullName() {
        final String fullName = name.getFullName();

        assertThat(fullName).isEqualTo("홍길동");
        assertThat(fullName).isNotEqualTo("길동홍");

        assertThat(fullName).contains("홍");
        assertThat(fullName).contains("길동");

        assertThat(fullName).doesNotContain("김");
    }
}