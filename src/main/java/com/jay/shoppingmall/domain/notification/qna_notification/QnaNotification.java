package com.jay.shoppingmall.domain.notification.qna_notification;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.notification.Notification;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_id", nullable = false)
    private Qna qna;

    @Enumerated(EnumType.STRING)
    private QnaCategory qnaCategory;

    private Boolean isSecret;

    private Boolean isAnswered;

    private String answer;

    private LocalDateTime answeredAt;

    @PrePersist
    public void prePersist() {
        this.isSecret = this.isSecret != null && this.isSecret;
        this.isAnswered = this.isAnswered != null && this.isAnswered;
        super.prePersist();
    }

    public void answerUpdate(String answer) {
        this.answer = answer;
        this.isAnswered = true;
        this.answeredAt = LocalDateTime.now();
    }
}
