package com.jay.shoppingmall.service.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommonServiceTest {

    @InjectMocks
    CommonService commonService;

    @Test
    void whenNameLengthIsEven_anonymousName() {
        String name = "qwerty";
        final String anonymousName = commonService.anonymousName(name);

        assertThat(anonymousName).isNotEqualTo(name);
        assertThat(anonymousName).isEqualTo("qwe***");
    }
    @Test
    void whenNameLengthIsOdd_anonymousName() {
        String name = "qwerty1";
        final String anonymousName = commonService.anonymousName(name);

        assertThat(anonymousName).isNotEqualTo(name);
        assertThat(anonymousName).isEqualTo("qwe***");
    }
}