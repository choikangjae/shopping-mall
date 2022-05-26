package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.qna.QnaCategory;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaResponse {

    private Long qnaId;

    private QnaCategory qnaCategory;

    private Boolean isSecret;

    private Boolean isAnswered;

    private Boolean isEmailNotification;

    private String question;

    private String answer;

    private String username;

    private Long itemId;
}
