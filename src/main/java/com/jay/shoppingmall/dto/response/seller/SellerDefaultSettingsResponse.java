package com.jay.shoppingmall.dto.response.seller;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerDefaultSettingsResponse {

    private Long id;

    private Long userId;

    private String companyName;
    private String contactNumber;

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
