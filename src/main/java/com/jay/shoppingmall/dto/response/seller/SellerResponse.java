package com.jay.shoppingmall.dto.response.seller;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "sellerId")
public class SellerResponse {

    private Long sellerId;

    private Long itemTotalPricePerSeller;

    private Integer itemTotalQuantityPerSeller;

    private String companyName;

    private Integer shippingFee;

    private Integer shippingFeeFreePolicy;

    private String defaultDeliveryCompany;

    public void setShippingFee(final Integer shippingFee) {
        this.shippingFee = shippingFee;
    }

    public void updateTotal(final Long itemTotalPricePerSeller, final Integer itemTotalQuantityPerSeller) {
        this.itemTotalPricePerSeller = itemTotalPricePerSeller;
        this.itemTotalQuantityPerSeller = itemTotalQuantityPerSeller;
    }
}
