package com.jay.shoppingmall.domain.order.order_item.order_delivery;

import com.jay.shoppingmall.domain.order.DeliveryStatus;
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
        this.deliveryStatus = DeliveryStatus.DELIVERING;
        this.deliveryStartDate = LocalDateTime.now();
    }
    public void deliveryDone() {
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.deliveryUpdateDate = LocalDateTime.now();
    }
    public void paymentDone() {
        this.deliveryStatus = DeliveryStatus.FINISHED;
        this.deliveryUpdateDate = LocalDateTime.now();
    }

}
