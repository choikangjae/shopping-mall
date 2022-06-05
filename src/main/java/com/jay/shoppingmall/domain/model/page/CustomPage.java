package com.jay.shoppingmall.domain.model.page;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage {

    private Long totalElements;

    private int totalPages;

    private int number;

    private int size;

    public CustomPage(Page<?> page) {
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
        this.size = page.getSize();
    }
}
