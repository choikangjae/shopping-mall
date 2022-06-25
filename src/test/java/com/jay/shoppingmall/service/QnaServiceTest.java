package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.notification.model.NotificationType;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotification;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotificationRepository;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaCategory;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.qna.QnaWriteRequest;
import com.jay.shoppingmall.dto.response.QnaResponse;
import com.jay.shoppingmall.exception.exceptions.QnaException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.common.CommonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QnaServiceTest {

    @InjectMocks
    QnaService qnaService;
    @Mock
    QnaRepository qnaRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    QnaNotificationRepository qnaNotificationRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    SellerService sellerService;
    @Mock
    CommonService commonService;

    Qna qna1;
    Qna qna2;
    User user;
    Seller seller;
    Item item;

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        seller = EntityBuilder.getSeller();
        item = EntityBuilder.getItem();

        qna1 = Qna.builder()
                .id(0L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user)
                .isEmailNotification(true)
                .isSecret(true)
                .question("질문")
                .answer("답변")
                .isAnswered(true)
                .build();
        qna2 = Qna.builder()
                .id(1L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user)
                .isEmailNotification(true)
                .isSecret(true)
                .question("질문")
                .isAnswered(false)
                .build();
    }

    @Test
    void whenIsAnsweredFalse_returnQna_qnaFindById() {
        when(qnaRepository.findById(0L)).thenReturn(Optional.ofNullable(qna2));

        final QnaResponse qnaResponse = qnaService.qnaFindById(0L);

        assertThat(qnaResponse.getQnaId()).isEqualTo(qna2.getId());
    }

    @Test
    void whenIsAnsweredTrue_ThrowQnaException_qnaFindById() {
        when(qnaRepository.findById(0L)).thenReturn(Optional.ofNullable(qna1));

        assertThrows(QnaException.class, () -> qnaService.qnaFindById(0L));
    }

    @Test
    void whenQnaIdIsNull_QnaWillBeSaved_qnaWrite() {
        QnaWriteRequest qnaWriteRequest = QnaWriteRequest.builder()
                .isEmailNotification(true)
                .isSecret(false)
                .question("질문")
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));

        final QnaResponse qnaResponse = qnaService.qnaWrite(qnaWriteRequest, user);

        verify(qnaRepository).save(any());
        verify(qnaNotificationRepository, never()).deleteByQnaId(any());
        verify(qnaNotificationRepository).save(any());
        assertThat(qnaResponse.getQuestion()).isNotNull();
    }

    @Test
    void whenQnaIdIsNotNull_QnaWillBeUpdated_qnaWrite() {
        QnaWriteRequest qnaWriteRequest = QnaWriteRequest.builder()
                .qnaId(1L)
                .isEmailNotification(true)
                .isSecret(false)
                .question("수정된 질문")
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .build();
        QnaNotification qnaNotification = QnaNotification.builder()
                .qna(qna2)
                .notificationType(NotificationType.QNA_TO_SELLER)
                .receiver(user)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(qnaRepository.findById(any())).thenReturn(Optional.ofNullable(qna2));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(qnaNotificationRepository.findByQnaId(any())).thenReturn(qnaNotification);

        final QnaResponse qnaResponse = qnaService.qnaWrite(qnaWriteRequest, user);

        verify(qnaRepository, never()).save(any());
        verify(qnaNotificationRepository).deleteByQnaId(any());
        verify(qnaNotificationRepository).save(any());
        assertThat(qnaResponse.getQuestion()).isEqualTo("수정된 질문");
        assertThat(qnaResponse.getQnaId()).isEqualTo(1L);
    }

    @Test
    void whenIsSecretTrue_IsQnaOwnerFalseAndSellerCheckFalse_QnaAndAnswerWillBeBlank_getQnas() {
        User user2 = EntityBuilder.getUser2();

        List<Qna> qnaList = new ArrayList<>();
        qnaList.add(qna1);
        qnaList.add(qna2);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Qna> qnaPage = new PageImpl<>(qnaList, pageable, qnaList.size());

        when(qnaRepository.findAllByItemId(any(), any())).thenReturn(qnaPage);
        when(sellerService.sellerCheck(any(), any())).thenReturn(false);
        when(commonService.anonymousName(any())).thenReturn("anonymous***");

        final PageDto qnas = qnaService.getQnas(0L, "", user2, Pageable.unpaged());
        @SuppressWarnings("unchecked")
        List<QnaResponse> qnaResponses = (List<QnaResponse>) qnas.getContent();

        assertThat(qnaResponses.get(0).getIsQnaOwner()).isFalse();
        assertThat(qnaResponses.get(0).getQuestion()).isEqualTo("");
        assertThat(qnaResponses.get(0).getAnswer()).isEqualTo("");

        assertThat(qnaResponses.get(1).getIsQnaOwner()).isFalse();
        assertThat(qnaResponses.get(1).getQuestion()).isEqualTo("");
        assertThat(qnaResponses.get(1).getAnswer()).isEqualTo("");

        assertThat(qnas.getContent().size()).isEqualTo(2);
    }
    @Test
    void whenIsSecretTrue_IsQnaOwnerTrueAndSellerCheckFalse_QnaAndAnswerWillBeShown_getQnas() {
        User user2 = EntityBuilder.getUser2();

        Qna qna2 = Qna.builder()
                .id(1L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user2)
                .isEmailNotification(true)
                .isSecret(true)
                .question("질문")
                .isAnswered(false)
                .build();

        List<Qna> qnaList = new ArrayList<>();
        qnaList.add(qna1);
        qnaList.add(qna2);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Qna> qnaPage = new PageImpl<>(qnaList, pageable, qnaList.size());

        when(qnaRepository.findAllByItemId(any(), any())).thenReturn(qnaPage);
        when(sellerService.sellerCheck(any(), any())).thenReturn(false);
        when(commonService.anonymousName(any())).thenReturn("anonymous***");

        final PageDto qnas = qnaService.getQnas(0L, "", user, Pageable.unpaged());
        @SuppressWarnings("unchecked")
        List<QnaResponse> qnaResponses = (List<QnaResponse>) qnas.getContent();

        assertThat(qnaResponses.get(0).getIsQnaOwner()).isTrue();
        assertThat(qnaResponses.get(0).getQuestion()).isEqualTo("질문");
        assertThat(qnaResponses.get(0).getAnswer()).isEqualTo("답변");

        assertThat(qnaResponses.get(1).getIsQnaOwner()).isFalse();
        assertThat(qnaResponses.get(1).getQuestion()).isEqualTo("");
        assertThat(qnaResponses.get(1).getAnswer()).isEqualTo("");

        assertThat(qnas.getContent().size()).isEqualTo(2);
    }
    @Test
    void whenIsSecretTrue_IsQnaOwnerFalseAndSellerCheckTrue_QnaAndAnswerWillBeShown_getQnas() {
        User user2 = EntityBuilder.getUser2();

        Qna qna2 = Qna.builder()
                .id(1L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user2)
                .isEmailNotification(true)
                .isSecret(true)
                .question("질문")
                .isAnswered(false)
                .build();

        List<Qna> qnaList = new ArrayList<>();
        qnaList.add(qna1);
        qnaList.add(qna2);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Qna> qnaPage = new PageImpl<>(qnaList, pageable, qnaList.size());

        when(qnaRepository.findAllByItemId(any(), any())).thenReturn(qnaPage);
        when(sellerService.sellerCheck(any(), any())).thenReturn(true);
        when(commonService.anonymousName(any())).thenReturn("anonymous***");

        final PageDto qnas = qnaService.getQnas(0L, "", user2, Pageable.unpaged());
        @SuppressWarnings("unchecked")
        List<QnaResponse> qnaResponses = (List<QnaResponse>) qnas.getContent();

        assertThat(qnaResponses.get(0).getIsQnaOwner()).isFalse();
        assertThat(qnaResponses.get(0).getQuestion()).isEqualTo("질문");
        assertThat(qnaResponses.get(0).getAnswer()).isEqualTo("답변");

        assertThat(qnas.getContent().size()).isEqualTo(2);
    }
    @Test
    void whenIsSecretFalse_IsQnaOwnerFalseAndSellerCheckFalse_QnaAndAnswerWillBeShown_getQnas() {
        Qna qna1 = Qna.builder()
                .id(0L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user)
                .isEmailNotification(true)
                .isSecret(false)
                .question("질문")
                .answer("답변")
                .isAnswered(true)
                .build();
        Qna qna2 = Qna.builder()
                .id(1L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user)
                .isEmailNotification(true)
                .isSecret(true)
                .question("질문")
                .isAnswered(false)
                .build();

        User user2 = EntityBuilder.getUser2();

        List<Qna> qnaList = new ArrayList<>();
        qnaList.add(qna1);
        qnaList.add(qna2);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Qna> qnaPage = new PageImpl<>(qnaList, pageable, qnaList.size());

        when(qnaRepository.findAllByItemId(any(), any())).thenReturn(qnaPage);
        when(sellerService.sellerCheck(any(), any())).thenReturn(false);
        when(commonService.anonymousName(any())).thenReturn("anonymous***");

        final PageDto qnas = qnaService.getQnas(0L, "", user2, Pageable.unpaged());
        @SuppressWarnings("unchecked")
        List<QnaResponse> qnaResponses = (List<QnaResponse>) qnas.getContent();

        assertThat(qnaResponses.get(0).getIsQnaOwner()).isFalse();
        assertThat(qnaResponses.get(0).getQuestion()).isEqualTo("질문");
        assertThat(qnaResponses.get(1).getQuestion()).isNotEqualTo("질문");
        assertThat(qnas.getContent().size()).isEqualTo(2);
    }

    @Test
    void whenSessionUserIdIsNotEqualToQnaUserId_ThrowUserNotFoundException_qnaDelete() {
        User user2 = EntityBuilder.getUser2();

        Qna qna2 = Qna.builder()
                .id(1L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user2)
                .isEmailNotification(true)
                .isSecret(true)
                .question("질문")
                .isAnswered(false)
                .build();

        when(qnaRepository.findById(any())).thenReturn(Optional.ofNullable(qna2));

        assertThrows(UserNotFoundException.class, () -> qnaService.qnaDelete(0L, user));
        verify(qnaRepository, never()).delete(any());
    }
    @Test
    void whenSessionUserIdIsEqualToQnaUserId_QnaWillBeDeleted_qnaDelete() {
        when(qnaRepository.findById(any())).thenReturn(Optional.ofNullable(qna1));

        qnaService.qnaDelete(0L, user);

        verify(qnaRepository).delete(any());
    }
}