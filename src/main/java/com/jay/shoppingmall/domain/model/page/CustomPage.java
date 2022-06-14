package com.jay.shoppingmall.domain.model.page;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
//@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class CustomPage {

    private String targetPage;
    //총 개수
    private Long totalElements;
    //전체 페이지
    private int totalPages;

    private int number;

    private int size;

    private long offset;

    //TODO layout + API + pagination 찾아볼것
    public CustomPage(Page<?> page, String targetPage) {
        this.targetPage = targetPage;
        this.offset = page.getPageable().getOffset();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
        this.size = page.getSize();
    }
}
