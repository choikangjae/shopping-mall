package com.jay.shoppingmall.domain.payment;

import com.jay.shoppingmall.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private Pg pg;

    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    private String merchantUid;

    //이름 생성 전략 생각할 것
    private String name;

    private Long amount;

    private String buyerEmail;

    private String buyerName;

    private String buyerTel;

    private String buyerAddr;

    private String buyerPostcode;

    private Boolean isValidated;

    private Boolean isAmountManipulated;

    public void isValidatedTrue() {
        this.isValidated = true;
    }

    public void isAmountFakeTrue() {
        this.isAmountManipulated = true;
    }
}
