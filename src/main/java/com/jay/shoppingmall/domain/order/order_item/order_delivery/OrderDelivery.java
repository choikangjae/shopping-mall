package com.jay.shoppingmall.domain.order.order_item.order_delivery;

import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.exception.exceptions.DeliveryException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    private LocalDateTime deliveryStartDate;

    private LocalDateTime deliveryUpdateDate;

    public void deliveryStart() {
        if (this.deliveryStatus.equals(DeliveryStatus.DELIVERING)) {
            throw new DeliveryException("이미 배송이 출발한 상품입니다");
        }
        this.deliveryStatus = DeliveryStatus.DELIVERING;
        this.deliveryStartDate = LocalDateTime.now();
    }
    public void deliveryDone() {
        if (this.deliveryStatus.equals(DeliveryStatus.DELIVERED)) {
            throw new DeliveryException("이미 배송이 완료된 상품입니다");
        }
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.deliveryUpdateDate = LocalDateTime.now();
    }
    public void paymentDone() {
        if (this.deliveryStatus.equals(DeliveryStatus.FINISHED)) {
            throw new DeliveryException("이미 결제가 완료된 상품입니다");
        }
        this.deliveryStatus = DeliveryStatus.FINISHED;
        this.deliveryUpdateDate = LocalDateTime.now();
    }

}
