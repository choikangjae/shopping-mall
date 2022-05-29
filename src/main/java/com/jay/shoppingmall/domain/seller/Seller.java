package com.jay.shoppingmall.domain.seller;

import com.jay.shoppingmall.exception.exceptions.AgreeException;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Boolean isSellerAgree;

    private Boolean isLawAgree;

    private Boolean isActivated;

    private Integer shippingFeePolicy;
}
