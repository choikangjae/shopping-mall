package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.notification.Notification;
import com.jay.shoppingmall.domain.notification.NotificationRepository;
import com.jay.shoppingmall.domain.notification.me_notification.MeNotification;
import com.jay.shoppingmall.domain.notification.model.NotificationType;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotification;
import com.jay.shoppingmall.domain.qna.QnaCategory;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.notification.MeNotificationResponse;
import com.jay.shoppingmall.dto.response.notification.QnaNotificationResponse;
import com.jay.shoppingmall.exception.exceptions.NotificationException;
import com.jay.shoppingmall.service.handler.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;
    @Mock
    NotificationRepository<Notification> notificationRepository;
    @Mock
    FileHandler fileHandler;
    @Mock
    ImageRepository imageRepository;
    User user;
    User user2;
    Notification notification;

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        user2 = EntityBuilder.getUser2();

        notification = MeNotification.builder()
                .receiver(user)
                .isRead(false)
                .build();
    }

    @Test
    void whenUserIdNotEqualToReceiverId_ThrowNotificationException_notificationReadDone() {
        when(notificationRepository.findById(any())).thenReturn(Optional.ofNullable(notification));

        assertThrows(NotificationException.class, () -> notificationService.notificationReadDone(0L, user2));
    }
    @Test
    void whenUserIdEqualToReceiverId_Success_notificationReadDone() {
        when(notificationRepository.findById(any())).thenReturn(Optional.ofNullable(notification));

        assertThat(notification.getIsRead()).isFalse();

        notificationService.notificationReadDone(0L, user);

        assertThat(notification.getIsRead()).isTrue();
    }

    @Test
    void getSellerRecentQnas() {
        List<QnaNotification> qnaNotificationList = new ArrayList<>();
        final QnaNotification qnaNotification = QnaNotification.builder()
                .item(Item.builder().id(10L).build())
                .notificationType(NotificationType.QNA_TO_SELLER)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .isRead(false)
                .isAnswered(false)
                .build();
        qnaNotificationList.add(qnaNotification);

        when(notificationRepository.findFirst5ByReceiverIdAndIsAnsweredFalseOrderBySentAtDesc(any())).thenReturn(qnaNotificationList);
        when(fileHandler.getStringImage(any())).thenReturn("이미지");

        final List<QnaNotificationResponse> sellerRecentQnas = notificationService.getSellerRecentQnas(user);

        assertThat(sellerRecentQnas.size()).isEqualTo(1);
        assertThat(sellerRecentQnas.get(0).isAnswered()).isFalse();
    }

    @Test
    void getMyRecentNotifications() {
        List<MeNotification> meNotificationList = new ArrayList<>();
        final MeNotification meNotification = MeNotification.builder()
                .item(Item.builder().id(10L).build())
                .notificationType(NotificationType.QNA_TO_SELLER)
                .originalMessage("Message")
                .isRead(false)
                .build();
        meNotificationList.add(meNotification);

        when(notificationRepository.findFirst5ByReceiverIdAndIsReadFalseOrderBySentAtDesc(any())).thenReturn(meNotificationList);

        final List<MeNotificationResponse> myRecentNotifications = notificationService.getMyRecentNotifications(user);

        assertThat(myRecentNotifications.size()).isEqualTo(1);
        assertThat(myRecentNotifications.get(0).getOriginalMessage()).isEqualTo("Message");
    }
}