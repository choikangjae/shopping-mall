package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.notification.Notification;
import com.jay.shoppingmall.domain.notification.NotificationRepository;
import com.jay.shoppingmall.domain.notification.me_notification.MeNotification;
import com.jay.shoppingmall.domain.notification.model.NotificationType;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotification;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.item.ItemSimpleResponse;
import com.jay.shoppingmall.dto.response.notification.MeNotificationResponse;
import com.jay.shoppingmall.dto.response.notification.NotificationResponse;
import com.jay.shoppingmall.dto.response.notification.QnaNotificationResponse;
import com.jay.shoppingmall.exception.exceptions.NotValidException;
import com.jay.shoppingmall.exception.exceptions.NotificationException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository<?> notificationRepository;
    private final ImageRepository imageRepository;

    private final FileHandler fileHandler;

    public void notificationReadDone(final Long notificationId, final User user) {
        final Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException("알림이 없음"));
        if (!notification.getReceiver().getId().equals(user.getId())) {
            log.info("Notification Exception. receiverId = '{}', userId = '{}'", notification.getReceiver().getId(), user.getId());
            throw new NotificationException("잘못된 요청입니다");
        }

        notification.readTrue();
    }

    @Transactional(readOnly = true)
    public List<QnaNotificationResponse> getSellerRecentQnas(final User user) {
        final List<QnaNotification> notifications = notificationRepository
                .findFirst5ByReceiverIdAndIsAnsweredFalseOrderBySentAtDesc(user.getId());

        List<QnaNotificationResponse> qnaNotificationResponses = new ArrayList<>();
        for (QnaNotification qnaNotification : notifications) {

            final String notificationType = NotificationType.QNA_TO_SELLER.getValue();
            NotificationResponse notificationResponse = getNotificationResponse(notificationType, qnaNotification.getMessage(), qnaNotification.getSentAt(), qnaNotification.getId());

            final Item item = qnaNotification.getItem();
            ItemSimpleResponse itemSimpleResponse = getItemSimpleResponse(item);

            final QnaNotificationResponse qnaNotificationResponse = QnaNotificationResponse.builder()
                    .notificationResponse(notificationResponse)
                    .itemSimpleResponse(itemSimpleResponse)
                    .qnaCategory(qnaNotification.getQnaCategory().value())
                    .isAnswered(false)
                    .build();

            if (qnaNotification.getIsRead()) {
                notificationResponse.setRead(qnaNotification.getReadAt());
            }
            if (qnaNotification.getIsAnswered()) {
                qnaNotificationResponse.setAnswer(qnaNotification.getAnsweredAt(), qnaNotification.getAnswer());
            }
            qnaNotificationResponses.add(qnaNotificationResponse);
        }
        return qnaNotificationResponses;
    }


    @Transactional(readOnly = true)
    public List<MeNotificationResponse> getMyRecentNotifications(final User user) {
        final List<MeNotification> meNotifications = notificationRepository.findFirst5ByReceiverIdAndIsReadFalseOrderBySentAtDesc(user.getId());

        List<MeNotificationResponse> meNotificationResponses = new ArrayList<>();
        for (MeNotification meNotification : meNotifications) {
            String notificationType = NotificationType.QNA_ANSWER_TO_USER.getValue();
            final NotificationResponse notificationResponse = getNotificationResponse(notificationType, meNotification.getMessage(), meNotification.getSentAt(), meNotification.getId());

            final Item item = meNotification.getItem();
            final ItemSimpleResponse itemSimpleResponse = getItemSimpleResponse(item);

            meNotificationResponses.add(MeNotificationResponse.builder()
                    .originalMessage(meNotification.getOriginalMessage())
                    .itemSimpleResponse(itemSimpleResponse)
                    .notificationResponse(notificationResponse)
                    .build());
        }
        return meNotificationResponses;

    }

    private ItemSimpleResponse getItemSimpleResponse(final Item item) {
        final Image image = imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, item.getId());
        final String stringImage = fileHandler.getStringImage(image);

        return ItemSimpleResponse.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .itemImage(stringImage)
                .build();
    }

    private NotificationResponse getNotificationResponse(final String notificationType, final String message, final LocalDateTime sentAt, final Long notificationId) {
        return NotificationResponse.builder()
                .notificationType(notificationType)
                .message(message)
                .sentAt(sentAt)
                .notificationId(notificationId)
                .isRead(false)
                .build();
    }

}
