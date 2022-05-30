package com.jay.shoppingmall.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaResponseWithPagination {

    private int totalPages;

    private long totalElements;

    private Boolean isSellerItem;

    private List<QnaResponse> qnaResponses;
}
