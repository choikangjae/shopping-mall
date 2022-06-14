package com.jay.shoppingmall.domain.notification.qna_notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QnaNotificationRepository extends JpaRepository<QnaNotification, Long> {
    QnaNotification findByQnaId(Long qnaId);

    void deleteByQnaId(Long qnaId);
}
