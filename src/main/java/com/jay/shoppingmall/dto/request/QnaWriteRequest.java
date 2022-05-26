package com.jay.shoppingmall.dto.request;

import com.jay.shoppingmall.domain.qna.QnaCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class QnaWriteRequest {

    private QnaCategory qnaCategory;

    private Boolean isSecret;

    private Boolean isEmailNotification;

    private String question;
}
