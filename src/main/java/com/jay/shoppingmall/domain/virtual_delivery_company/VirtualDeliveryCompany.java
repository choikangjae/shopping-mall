package com.jay.shoppingmall.domain.virtual_delivery_company;

import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VirtualDeliveryCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String packageName;

    private String trackingNumber;

    private String senderName;

    private String senderAddress;

    private String senderPhoneNumber;

    private String senderPostcode;

    private String receiverName;

    private String receiverAddress;

    private String receiverPhoneNumber;

    private String receiverPostcode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setOrderItem(final OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
