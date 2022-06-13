package com.jay.shoppingmall.dto.response.seller;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticsResponse {

    private LocalDateTime date;
    private Long totalPricePerDay;
    private Long totalQuantityPerDay;
    private Long totalOrderPerDay;
}
