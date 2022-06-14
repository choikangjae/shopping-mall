package com.jay.shoppingmall.domain.qna;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Entity
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE qna SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
public class Qna extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QnaCategory qnaCategory;

    private Boolean isSecret;

    private Boolean isAnswered;

    private Boolean isEmailNotification;

    @Column(nullable = false)
    @Size(min = 4, max = 500, message = "4글자 이상 작성해주세요")
    private String question;

    @Size(min = 4, max = 500)
    private String answer;

    private LocalDateTime answeredAt;

    @Column(columnDefinition = "boolean default 0")
    private Boolean isDeleted;

    private LocalDateTime deletedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void QnaDirtyChecker(final QnaCategory qnaCategory, final Boolean isSecret, final Boolean isEmailNotification, final String question) {
        this.qnaCategory = qnaCategory;
        this.isSecret = isSecret;
        this.isEmailNotification = isEmailNotification;
        this.question = question;
    }

    public void answerUpdate(final String answer) {
        this.answer = answer;
        this.isAnswered = true;
        this.answeredAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.isSecret = this.isSecret != null && this.isSecret;
        this.isAnswered = this.isAnswered != null && this.isAnswered;
        this.isEmailNotification = this.isEmailNotification != null && this.isEmailNotification;
        this.isDeleted = this.isDeleted != null && this.isDeleted;
    }

}
