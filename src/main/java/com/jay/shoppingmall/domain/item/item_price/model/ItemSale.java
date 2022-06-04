package com.jay.shoppingmall.domain.item.item_price.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSale {

    private Long salePrice;

    private LocalDateTime saleFinishDate;

    private LocalDateTime saleStartDate;
}
