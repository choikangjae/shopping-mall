package com.jay.shoppingmall.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerDefaultSettingsResponse {

    private Long id;

    private Long userId;

    private String companyName;

    private String itemReleaseZipcode;
    private String itemReleaseAddress;
    private String itemReleaseDetailAddress;
    private String itemReleaseExtraAddress;

    private String itemReturnZipcode;
    private String itemReturnAddress;
    private String itemReturnDetailAddress;
    private String itemReturnExtraAddress;

    private Integer shippingFeeDefault;

    private Integer returnShippingFeeDefault;

    private Integer shippingFeeFreePolicy;

    private String defaultDeliveryCompany;

}
