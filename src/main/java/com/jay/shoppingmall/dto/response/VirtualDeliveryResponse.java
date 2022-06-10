package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VirtualDeliveryResponse {

    private Long virtualDeliveryCompanyId;

    private String trackingNumber;

    private String packageName;

    private String senderName;

    private String senderAddress;

    private String senderPhoneNumber;

    private String senderPostcode;

    private String receiverName;

    private String receiverAddress;

    private String receiverPhoneNumber;

    private String receiverPostcode;

    private Long sellerId;

    private Long userId;
}
