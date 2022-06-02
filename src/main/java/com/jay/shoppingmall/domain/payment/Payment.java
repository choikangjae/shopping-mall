package com.jay.shoppingmall.domain.payment;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.payment.model.PayMethod;
import com.jay.shoppingmall.domain.payment.model.Pg;
import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
import com.jay.shoppingmall.domain.user.User;
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

//    private Long userId;

    @Enumerated(EnumType.STRING)
    private Pg pg;

    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    private String merchantUid;

    private String name;

    private Long amount;

    private String buyerEmail;

    private String buyerName;

    private String buyerTel;

    private String buyerAddr;

    private String buyerPostcode;

    private Boolean isValidated;

    private Boolean isAmountManipulated;

    @Embedded
    private ReceiverInfo receiverInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public void isValidatedTrue() {
        this.isValidated = true;
    }

    public void isAmountManipulatedTrue() {
        this.isAmountManipulated = true;
    }
}
