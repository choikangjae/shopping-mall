package com.jay.shoppingmall.domain.qna;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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
    @Max(500)
    private String question;

    @Max(500)
    private String answer;

    @Builder
    public Qna(final QnaCategory qnaCategory, final Boolean isSecret, final Boolean isAnswered, final Boolean isEmailNotification, final String question, final String answer) {
        this.qnaCategory = qnaCategory;
        this.isSecret = isSecret;
        this.isAnswered = isAnswered;
        this.isEmailNotification = isEmailNotification;
        this.question = question;
        this.answer = answer;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

}
