package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZzimServiceTest {

    @InjectMocks
    ZzimService zzimService;
    @Mock
    ZzimRepository zzimRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
    }

    @Test
    void whenIsZzimedTrue_ReturnTrue_isZzimed() {
        Zzim zzim = Zzim.builder()
                .isZzimed(true)
                .build();

        when(zzimRepository.findByUserIdAndItemId(anyLong(), anyLong())).thenReturn(zzim);

        final boolean zzimed = zzimService.isZzimed(user, 0L);

        assertThat(zzimed).isTrue();
    }
    @Test
    void whenIsZzimedFalse_ReturnFalse_isZzimed() {
        Zzim zzim = Zzim.builder()
                .isZzimed(false)
                .build();

        when(zzimRepository.findByUserIdAndItemId(anyLong(), anyLong())).thenReturn(zzim);

        final boolean zzimed = zzimService.isZzimed(user, 0L);

        assertThat(zzimed).isFalse();
    }
}