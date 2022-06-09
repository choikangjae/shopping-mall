package com.jay.shoppingmall.domain.payment.payment_per_seller;

import com.jay.shoppingmall.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentPerSellerRepository extends JpaRepository<PaymentPerSeller, Long> {

    List<PaymentPerSeller> findByPayment(Payment payment);

    List<PaymentPerSeller> findTop20BySellerId(Long sellerId);
}
