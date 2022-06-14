package com.jay.shoppingmall.domain.notification;

import com.jay.shoppingmall.domain.notification.item_notification.ItemNotification;
import com.jay.shoppingmall.domain.notification.me_notification.MeNotification;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository<T extends Notification> extends JpaRepository<T, Long> {
    List<QnaNotification> findFirst5ByReceiverIdAndIsAnsweredFalseOrderBySentAtDesc(Long receiverId);

    QnaNotification findByQnaId(Long qnaId);

    List<MeNotification> findFirst5ByReceiverIdAndIsReadFalseOrderBySentAtDesc(Long receiverId);
}
