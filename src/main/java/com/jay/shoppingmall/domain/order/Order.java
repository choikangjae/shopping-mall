package com.jay.shoppingmall.domain.order;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.PaymentType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //1toN
    @OneToMany(mappedBy = "order")
    private List<Item> itemList = new ArrayList<>();

    //1to1
    private Payment payment;

    private DeliveryStatus deliveryStatus;

}
