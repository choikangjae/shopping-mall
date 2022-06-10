package com.jay.shoppingmall.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerDefaultSettingsRequest {
    @NotBlank
    private String companyName;

    @Setter
    @NotBlank
    private String contactNumber;

    @NotNull
    private Integer shippingFeeDefault;

    @NotNull
    private Integer returnShippingFeeDefault;

    private Integer shippingFeeFreePolicy;

    @NotBlank
    private String defaultDeliveryCompany;

    @NotBlank
    private String itemReleaseZipcode;
    @NotBlank
    private String itemReleaseAddress;
    @NotBlank
    private String itemReleaseDetailAddress;
    private String itemReleaseExtraAddress;

    private String itemReturnZipcode;
    private String itemReturnAddress;
    private String itemReturnDetailAddress;
    private String itemReturnExtraAddress;
}
