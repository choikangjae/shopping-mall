package com.jay.shoppingmall.dto.request.qna;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaPageRequest {

    @NotNull
    private Integer requestPage;

    @NotNull
    private Integer perPage;

    @NotNull
    private Long itemId;

}
