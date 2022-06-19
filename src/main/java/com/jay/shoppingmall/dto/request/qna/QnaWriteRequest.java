package com.jay.shoppingmall.dto.request.qna;

import com.jay.shoppingmall.domain.qna.QnaCategory;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class QnaWriteRequest {

    @NotNull
    private Long itemId;

    private Long qnaId;

    @NotNull
    private QnaCategory qnaCategory;

    @NotNull
    private Boolean isSecret;

    @NotNull
    private Boolean isEmailNotification;

    @Size(min = 4, max = 500, message = "4글자 이상 작성해주세요")
    @NotNull
    private String question;
}
