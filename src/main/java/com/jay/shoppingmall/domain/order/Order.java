package com.jay.shoppingmall.domain.order;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", referencedColumnName = "id", nullable = false)
    private Payment payment;
}
