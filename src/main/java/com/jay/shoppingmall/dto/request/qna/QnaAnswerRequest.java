package com.jay.shoppingmall.dto.request.qna;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaAnswerRequest {

    @NotBlank
    private String answer;

    @NotNull
    private Long qnaId;
}
