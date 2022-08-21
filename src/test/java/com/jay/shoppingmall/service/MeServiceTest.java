package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.AgreeRequest;
import com.jay.shoppingmall.dto.request.UserUpdateRequest;
import com.jay.shoppingmall.dto.response.MeDetailResponse;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeServiceTest {

    @InjectMocks
    MeService meService;
    @Mock
    UserRepository userRepository;

    User user;
    final String PHONENUMBER = "010-1234-5678";

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
    }

    @Test
    void whenUserExists_ReturnUser_findById() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        final MeDetailResponse meDetailResponse = meService.findById(0L);

        assertThat(meDetailResponse.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void whenIsMandatoryAgreeFalse_ThrowAgreeException_agreeCheck() {
        AgreeRequest agreeRequest = new AgreeRequest(false, false);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));

        assertThrows(AgreeException.class, () -> meService.agreeCheck(agreeRequest, 0L));
    }

    @Test
    void whenIsMandatoryAgreeTrue_ReturnTrue_agreeCheck() {
        AgreeRequest agreeRequest = new AgreeRequest(true, false);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));

        final Boolean agreeCheck = meService.agreeCheck(agreeRequest, 0L);

        assertThat(agreeCheck).isTrue();
        assertThat(user.getAgree().getIsMandatoryAgree()).isTrue();
        assertThat(user.getAgree().getIsMarketingAgree()).isFalse();
    }

    @Test
    void updateInfo() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .address("주소")
                .detailAddress("상세주소")
                .extraAddress("추가주소")
                .phoneNumber(PHONENUMBER)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));

        meService.updateInfo(userUpdateRequest, 0L);

        verify(userRepository).save(any());
    }

    @Test
    void deleteMe() {
        meService.deleteMe(user);

        verify(userRepository).delete(any());
    }

    @Test
    void whenPhoneNumberExists_ThrowAlreadyExistsException_duplicationCheck() {
        when(userRepository.findByPhoneNumber(any(), any(), any())).thenReturn(Optional.ofNullable(user));

        assertThrows(AlreadyExistsException.class, () -> meService.duplicationCheck(PHONENUMBER));
    }
    @Test
    void whenPhoneNumberNotExists_NoInvocation_duplicationCheck() {
        meService.duplicationCheck(PHONENUMBER);
    }
}