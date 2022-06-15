package com.jay.shoppingmall.domain.model.page;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
//@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class CustomPage {

    private String targetPage;

    private Long totalElements;
    private int totalPages;

    private int number;
    private int size;

    private long offset;

    private boolean previousPage;
    private boolean firstPage;
    private boolean nextPage;
    private boolean lastPage;

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
