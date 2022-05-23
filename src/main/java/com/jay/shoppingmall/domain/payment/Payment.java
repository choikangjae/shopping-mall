package com.jay.shoppingmall.domain.payment;

import com.jay.shoppingmall.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

//    private Boolean isShippingFeeFree;
}
