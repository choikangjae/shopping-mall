package com.jay.shoppingmall.domain.payment.payment_per_seller;

import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.exception.exceptions.MoneyTransactionException;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentPerSeller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long itemTotalPricePerSeller;
    @Column(columnDefinition = "boolean default 0")
    private Boolean isMoneyTransferredToSeller = false;

    private Integer itemTotalQuantityPerSeller;

    private Integer itemShippingFeePerSeller;
    @Column(columnDefinition = "boolean default 0")
    private Boolean isMoneyTransferredToDeliveryCompany = false;

//    @OneToMany(mappedBy = "paymentPerSeller")
//    @Builder.Default
//    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    private Order order;

    @Builder
    public PaymentPerSeller(final Long itemTotalPricePerSeller, final Integer itemTotalQuantityPerSeller, final Integer itemShippingFeePerSeller, final Payment payment, final Seller seller, final Order order) {
        this.itemTotalPricePerSeller = itemTotalPricePerSeller;
        this.itemTotalQuantityPerSeller = itemTotalQuantityPerSeller;
        this.itemShippingFeePerSeller = itemShippingFeePerSeller;
        this.seller = seller;
        this.order = order;
//        this.orderItems = orderItems;
        paymentPerSeller(payment);
    }

    //조회하기 위해 추가
    private void paymentPerSeller(final Payment payment) {
        this.payment = payment;
        payment.getPaymentPerSellers().add(this);
    }

    public void paymentToSellerTrue() {
        if (this.isMoneyTransferredToSeller) {
            throw new MoneyTransactionException("이미 정산이 완료된 주문입니다");
        }
        this.isMoneyTransferredToSeller = true;
    }
}
