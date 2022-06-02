package com.jay.shoppingmall.domain.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Getter
@Component
@AllArgsConstructor
public class MerchantUidGenerator {
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
                System.nanoTime();
//                String.format("%07d", ++i);
    }
}
