package com.jay.shoppingmall.domain.qna;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
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
    @Size(min = 4, max = 500, message = "4글자 이상 작성해주세요")
    private String question;

    @Size(min = 4, max = 500)
    private String answer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

}
