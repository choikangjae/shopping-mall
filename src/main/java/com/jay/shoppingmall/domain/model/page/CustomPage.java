package com.jay.shoppingmall.domain.model.page;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage {

    //총 개수
    private Long totalElements;
    //전체 페이지
    private int totalPages;

    private int number;

    private int size;

    private long offset;

    public CustomPage(Page<?> page) {
        this.offset = page.getPageable().getOffset();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
        this.size = page.getSize();
    }
}
