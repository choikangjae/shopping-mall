package com.jay.shoppingmall.domain.model.page;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageDto{

    List<?> content;

    CustomPage customPage;

    @Builder
    public PageDto(final List<?> content, final CustomPage customPage) {
        this.content = content;
        this.customPage = customPage;
    }
}
