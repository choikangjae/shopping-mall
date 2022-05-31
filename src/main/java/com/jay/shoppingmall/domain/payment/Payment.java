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

    @Enumerated(EnumType.STRING)
    private Pg pg;

    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    //ORD + yyyy-MM-dd + - + 7자리 숫자
    private String merchantUid;

//    private String name;

    private Long amount;

    private String buyerEmail;

    private String buyerName;

    private String buyerTel;

    private String buyerAddr;

    private String buyerPostcode;

    private static class MerchantUidGenerator {
        private static int i;
        private static LocalDate localDate = LocalDate.now();

        public String generateMerchantUid() {
            if (!localDate.isEqual(ChronoLocalDate.from(LocalDateTime.now()))) {
                i = 0;
                localDate = LocalDate.now();
            }
            return "ORD" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                    "-" +
                    String.format("%07d", ++i);
        }

        public MerchantUidGenerator() {
        }
    }


}
