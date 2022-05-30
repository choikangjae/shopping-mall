package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.qna.QnaCategory;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaResponse {

    private Long qnaId;

    private String qnaCategory;

    @Setter
    private Boolean isSecret;

    private Boolean isAnswered;

    private Boolean isEmailNotification;

    @Setter
    private String question;

    @Setter
    private String answer;

    private String username;

    private String email;

    private Long itemId;

    private LocalDateTime createdDate;

    @Setter
    private Boolean isQnaOwner;
}
