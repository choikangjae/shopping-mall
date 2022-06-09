package com.jay.shoppingmall.domain.payment.payment_per_seller;

import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.seller.Seller;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentPerSeller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long itemTotalPricePerSeller;

    private Integer itemTotalQuantityPerSeller;

    private Integer itemShippingFeePerSeller;

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
        paymentPerSeller(payment);
    }

    //조회하기 위해 추가
    private void paymentPerSeller(final Payment payment) {
        this.payment = payment;
        payment.getPaymentPerSellers().add(this);
    }
}
