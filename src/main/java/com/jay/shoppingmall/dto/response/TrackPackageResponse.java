package com.jay.shoppingmall.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackPackageResponse {

    private VirtualDeliveryResponse virtualDeliveryResponse;

    private VirtualDeliveryResponseForAnonymous virtualDeliveryResponseForAnonymous;
}
